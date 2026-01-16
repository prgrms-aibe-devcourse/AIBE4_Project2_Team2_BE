package kr.java.aibe4_project2_team2_be.majormate.domain.recommend.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.recommend.service.AiRecommendService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendController {

    private final AiRecommendService aiRecommendService;

    @GetMapping
    public ApiResponseNew<List<MajorProfileResponse>> getRecommendations() {
        // 1. 로그인 유저 ID
        Long studentId = SecurityUtil.getCurrentMemberId();

        // 2. AI 서비스 호출
        var recommendedMentors = aiRecommendService.getRecommendations(studentId);

        // 3. Response DTO 변환
        List<MajorProfileResponse> response = recommendedMentors.stream()
                .map(MajorProfileResponse::from)
                .toList();

        return ApiResponseNew.success(response);
    }
}