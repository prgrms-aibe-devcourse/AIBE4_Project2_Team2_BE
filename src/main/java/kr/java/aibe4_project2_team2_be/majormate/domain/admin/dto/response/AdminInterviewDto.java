package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import lombok.Getter;

@Getter
public class AdminInterviewDto {
    private final Long interviewId;
    private final String title;
    private final String studentName;
    private final String majorName;
    private final String status; // 인터뷰 상태 (PENDING, ACCEPTED, COMPLETED)
    private final String method;
    private final LocalDateTime createdAt;

    public AdminInterviewDto(InterviewForm interview, String studentName, String majorName) {
        this.interviewId = interview.getInterviewId();
        this.title = interview.getTitle();
        this.studentName = studentName;
        this.majorName = majorName;
        this.status = interview.getStatus().name();
        this.method = interview.getInterviewMethod();
        this.createdAt = interview.getCreatedAt();
    }
}