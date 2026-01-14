package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record PublicReviewResponse(
	PeerInfo peer,
	ReviewBody review,
	InterviewBody interview,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record PeerInfo(
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
	}

	public record ReviewBody(
		int rating,
		String content
	) {
	}

	public record InterviewBody(
		InterviewFormStatus status
	) {
	}

	public static PublicReviewResponse receivedSummary(
		Review review,
		InterviewForm form,
		InterviewStudentSnapshot student
	) {
		return new PublicReviewResponse(
			new PeerInfo(
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
			),
			new ReviewBody(
				review.getRating(),
				review.getContent()
			),
			new InterviewBody(
				form.getStatus()
			),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
