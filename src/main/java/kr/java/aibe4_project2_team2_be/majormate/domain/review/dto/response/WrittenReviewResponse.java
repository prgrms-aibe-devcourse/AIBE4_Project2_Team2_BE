package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;

public record WrittenReviewResponse(
	Long reviewId,
	Long interviewId,
	MajorInfo major,
	int rating,
	String content,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record MajorInfo(
		Long majorMemberId,
		String profileImageUrl,
		String nickname,
		String university,
		String major
	) {
	}

	public static WrittenReviewResponse from(Review review, InterviewForm form, InterviewMajorSnapshot majorSnapshot) {
		return new WrittenReviewResponse(
			review.getReviewId(),
			review.getInterviewId(),
			new MajorInfo(
				form.getMajorMemberId(),
				majorSnapshot.getProfileImageUrl(),
				majorSnapshot.getNickname(),
				majorSnapshot.getUniversity(),
				majorSnapshot.getMajor()
			),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
