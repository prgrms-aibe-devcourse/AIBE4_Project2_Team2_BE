package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class AdminReviewDetailDto {
    private final Long reviewId;
    private final Long interviewId;
    private final String studentName;
    private final String majorName;
    private final int rating;
    private final String content;
    private final LocalDateTime createdAt;

    public AdminReviewDetailDto(Review review, String studentName, String majorName) {
        this.reviewId = review.getReviewId();
        this.interviewId = review.getInterviewId(); // 상세니까 인터뷰 ID도 보여줌
        this.studentName = studentName;
        this.majorName = majorName;
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}