package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AdminQuestionDetailDto {

    private Long questionId;
    private String studentName;
    private String majorName;
    private String content;
    private boolean hasAnswer;
    private String answerContent;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;

    public AdminQuestionDetailDto(Question question, String studentName, String majorName) {
        this.questionId = question.getQuestionId();
        this.studentName = studentName;
        this.majorName = majorName;
        this.content = question.getContent();
        this.hasAnswer = question.isHasAnswer();
        this.createdAt = question.getCreatedAt();

        if (question.getAnswer() != null) {
            this.answerContent = question.getAnswer().getContent();
            this.answeredAt = question.getAnswer().getCreatedAt();
        }
    }
}