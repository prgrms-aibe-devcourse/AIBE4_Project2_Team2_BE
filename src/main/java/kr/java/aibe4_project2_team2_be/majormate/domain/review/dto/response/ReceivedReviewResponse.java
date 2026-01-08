package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;

public record ReceivedReviewResponse(
	Long reviewId,
	Long interviewId,
	StudentInfo studentInfo,
	int rating,
	String content,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record StudentInfo(
		String profileImageUrl,
		String nickname
	) {
	}

	public static ReceivedReviewResponse from(Review review, InterviewStudentSnapshot snapshot) {
		return new ReceivedReviewResponse(
			review.getReviewId(),
			review.getInterviewId(),
			new StudentInfo(
				snapshot.getProfileImageUrl(),
				snapshot.getNickname()
			),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
