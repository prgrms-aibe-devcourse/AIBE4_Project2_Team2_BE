package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class AdminReviewDto {
    private final Long reviewId;
    private final String studentName;
    private final String majorName;
    private final int rating;
    private final String content; // 목록에서는 짧게 보여줄 예정
    private final LocalDateTime createdAt;

    public AdminReviewDto(Review review, String studentName, String majorName) {
        this.reviewId = review.getReviewId();
        this.studentName = studentName;
        this.majorName = majorName;
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}