package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AdminQuestionDto {

    private Long questionId;
    private String contentSummary;
    private String studentName;
    private String majorName;
    private boolean hasAnswer;
    private LocalDateTime createdAt;

    public AdminQuestionDto(Question question, String studentName, String majorName) {
        this.questionId = question.getQuestionId();

        this.studentName = studentName;
        this.majorName = majorName;
        this.hasAnswer = question.isHasAnswer();
        this.createdAt = question.getCreatedAt();
        String originContent = question.getContent();
        this.contentSummary = (originContent != null && originContent.length() > 20)
                ? originContent.substring(0, 20) + "..."
                : originContent;
    }
}