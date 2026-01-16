package kr.java.aibe4_project2_team2_be.majormate.domain.recommend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.recommend.dto.GeminiMessageDto;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendService {

    private final MajorProfileRepository majorProfileRepository;
    private final MemberAcademicRepository memberAcademicRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public List<MajorProfile> getRecommendations(Long studentId) {

        MemberAcademic studentInfo = memberAcademicRepository.findByMemberProfile_MemberId(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_404));

        List<MajorProfile> allMentors = majorProfileRepository.findAllActiveMentorsWithTags();
        if (allMentors.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> mentorDataList = allMentors.stream().map(m -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", m.getMajorProfileId());
            data.put("major", m.getMemberProfile().getAcademic().getMajor());
            data.put("introduction", m.getContent()); // ★ 한줄 소개
            data.put("tags", m.getTags().stream().map(t -> t.getTagName()).toList());
            return data;
        }).toList();

        try {
            String prompt = createPrompt(studentInfo.getMajor(), mentorDataList);
            List<Long> recommendedIds = callGemini(prompt);

            if (recommendedIds == null || recommendedIds.isEmpty()) {
                throw new RuntimeException("AI 추천 결과 없음");
            }

            List<MajorProfile> result = allMentors.stream()
                    .filter(m -> recommendedIds.contains(m.getMajorProfileId()))
                    .sorted(Comparator.comparingInt(m -> recommendedIds.indexOf(m.getMajorProfileId())))
                    .collect(Collectors.toList());

            if (result.isEmpty()) throw new RuntimeException("추천된 ID 매칭 실패");

            return result;

        } catch (Exception e) {
            log.warn("AI 추천 실패. 기본 추천(최신순)으로 대체. Error: {}", e.getMessage());

            // 에러 시 전공자 리스트에서 최신순 3명 반환
            return allMentors.stream()
                    .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                    .limit(3)
                    .collect(Collectors.toList());
        }
    }

    private String createPrompt(String studentMajor, List<Map<String, Object>> mentorData) throws JsonProcessingException {
        String mentorsJson = objectMapper.writeValueAsString(mentorData);
        //gemini산 프롬프트 검색
        return """
            You are an AI assistant for a college major mentoring platform.
            Your task is to recommend the top 3 mentors for a student based on their major interest.
            
            [Student Info]
            Interested Major: %s
            
            [Mentor Candidates]
            %s
            
            [Instruction]
            1. Analyze the student's interested major and match it with mentors' majors, tags, and 'introduction'.
            2. The 'introduction' field contains the mentor's self-description. Use this to gauge relevance.
            3. Select the top 3 most relevant mentors.
            4. Return ONLY a JSON Array containing the 'id' (integers) of the selected mentors.
            5. If no mentors are relevant, select any 3 mentors randomly.
            6. Do not include any explanation or markdown formatting.
            
            Example Output:
            [12, 5, 8]
            """.formatted(studentMajor, mentorsJson);
    }

    private List<Long> callGemini(String prompt) {
        GeminiMessageDto.Request requestBody = GeminiMessageDto.Request.builder()
                .contents(List.of(GeminiMessageDto.Request.Content.builder()
                        .role("user")
                        .parts(List.of(GeminiMessageDto.Request.Part.builder().text(prompt).build()))
                        .build()))
                .generationConfig(GeminiMessageDto.Request.GenerationConfig.builder()
                        .responseMimeType("application/json") // ★ JSON 응답 강제
                        .build())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiMessageDto.Request> entity = new HttpEntity<>(requestBody, headers);
        GeminiMessageDto.Response response = restTemplate.postForObject(GEMINI_URL + geminiApiKey, entity, GeminiMessageDto.Response.class);

        if (response != null && !response.getCandidates().isEmpty()) {
            String rawText = response.getCandidates().get(0).getContent().getParts().get(0).getText();
            return parseJsonIds(rawText);
        }
        return Collections.emptyList();
    }

    private List<Long> parseJsonIds(String text) {
        try {
            String cleanJson = text.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(cleanJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 오류: {}", text);
            return Collections.emptyList();
        }
    }
}