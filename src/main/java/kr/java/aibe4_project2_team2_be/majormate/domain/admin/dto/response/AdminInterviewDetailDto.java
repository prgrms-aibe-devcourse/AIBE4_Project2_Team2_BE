package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import lombok.Getter;

@Getter
public class AdminInterviewDetailDto {
    private final Long interviewId;
    private final String title;
    private final String content; // 질문 내용
    private final String studentName;
    private final String majorName;
    private final String status;
    private final String method;
    private final LocalDateTime preferredDatetime; // 희망 시간
    private final String extraDescription; // 추가 요청 사항
    private final String majorMessage; // 전공자의 수락/거절 메시지
    private final LocalDateTime createdAt;

    public AdminInterviewDetailDto(InterviewForm interview, String studentName, String majorName) {
        this.interviewId = interview.getInterviewId();
        this.title = interview.getTitle();
        this.content = interview.getContent();
        this.studentName = studentName;
        this.majorName = majorName;
        this.status = interview.getStatus().name();
        this.method = interview.getInterviewMethod();
        this.preferredDatetime = interview.getPreferredDatetime();
        this.extraDescription = interview.getExtraDescription();
        this.majorMessage = interview.getMajorMessage();
        this.createdAt = interview.getCreatedAt();
    }
}