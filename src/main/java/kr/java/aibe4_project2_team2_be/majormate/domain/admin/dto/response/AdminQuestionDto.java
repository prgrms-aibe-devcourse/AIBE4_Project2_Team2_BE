package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminQuestionDto {
    private Long questionId;
    private String contentPreview; // 제목 대신 내용 미리보기
    private String studentName;    // 질문자 (학생)
    private String majorName;      // 질문 대상 (전공자)
    private LocalDateTime createdAt;
    private boolean hasAnswer;     // 답변 상태

    public static AdminQuestionDto from(Question question) {
        // 내용이 길면 잘라서 보여줌 (제목 대용)
        String content = question.getContent();
        String preview = content.length() > 20 ? content.substring(0, 20) + "..." : content;
        return AdminQuestionDto.builder()
                .questionId(question.getQuestionId())
                .contentPreview(preview)
                .studentName(question.getStudent().getNickname())
                .majorName(question.getMajor().getNickname())
                .createdAt(question.getCreatedAt())
                .hasAnswer(question.isHasAnswer())
                .build();
    }
}