package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminQuestionDetailDto {
    // 질문 정보
    private Long questionId;
    private String content;
    private String studentName; // 질문자
    private String majorName;   // 질문 대상
    private LocalDateTime createdAt;
    private boolean hasAnswer;

    // 답변 정보 (null 가능)
    private Long answerId;
    private String answerContent;
    private LocalDateTime answerCreatedAt;

    public static AdminQuestionDetailDto from(Question question) {
        Answer answer = question.getAnswer();

        return AdminQuestionDetailDto.builder()
                .questionId(question.getQuestionId())
                .content(question.getContent())
                .studentName(question.getStudent().getNickname())
                .majorName(question.getMajor().getNickname())
                .createdAt(question.getCreatedAt())
                .hasAnswer(question.isHasAnswer())
                .answerId(answer != null ? answer.getAnswerId() : null)
                .answerContent(answer != null ? answer.getContent() : null)
                .answerCreatedAt(answer != null ? answer.getCreatedAt() : null)
                .build();
    }
}