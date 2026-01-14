package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record ReviewResponse(
	ViewType viewType,
	PeerInfo peer,
	ReviewBody review,
	InterviewBody interview,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public enum ViewType {
		WRITTEN, RECEIVED
	}

	public record PeerInfo(
		Long memberId,
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
	}

	public record ReviewBody(
		Long reviewId,
		int rating,
		String content
	) {
	}

	public record InterviewBody(
		Long interviewId,
		String title,
		String content,
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription,
		String majorMessage,
		InterviewFormStatus status
	) {
	}

	public static ReviewResponse writtenSummary(Review review, InterviewForm form, InterviewMajorSnapshot major) {
		return new ReviewResponse(
			ViewType.WRITTEN,
			new PeerInfo(
				form.getMajorMemberId(),
				major.getProfileImageUrl(),
				major.getNickname(),
				major.getStatus(),
				major.getUniversity(),
				major.getMajor()
			),
			new ReviewBody(
				review.getReviewId(),
				review.getRating(),
				review.getContent()
			),
			new InterviewBody(
				form.getInterviewId(),
				form.getTitle(),
				null,
				null,
				null,
				null,
				null,
				null
			),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	public static ReviewResponse writtenDetail(Review review, InterviewForm form, InterviewMajorSnapshot major) {
		return new ReviewResponse(
			ViewType.WRITTEN,
			new PeerInfo(
				form.getMajorMemberId(),
				major.getProfileImageUrl(),
				major.getNickname(),
				major.getStatus(),
				major.getUniversity(),
				major.getMajor()
			),
			new ReviewBody(
				review.getReviewId(),
				review.getRating(),
				review.getContent()
			),
			new InterviewBody(
				form.getInterviewId(),
				form.getTitle(),
				form.getContent(),
				form.getInterviewMethod(),
				form.getPreferredDatetime(),
				form.getExtraDescription(),
				form.getMajorMessage(),
				form.getStatus()
			),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	public static ReviewResponse receivedSummary(Review review, InterviewForm form, InterviewStudentSnapshot student) {
		return new ReviewResponse(
			ViewType.RECEIVED,
			new PeerInfo(
				form.getStudentMemberId(),
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
			),
			new ReviewBody(
				review.getReviewId(),
				review.getRating(),
				review.getContent()
			),
			new InterviewBody(
				form.getInterviewId(),
				form.getTitle(),
				null,
				null,
				form.getPreferredDatetime(),
				null,
				null,
				null
			),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	public static ReviewResponse receivedDetail(Review review, InterviewForm form, InterviewStudentSnapshot student) {
		return new ReviewResponse(
			ViewType.RECEIVED,
			new PeerInfo(
				form.getStudentMemberId(),
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
			),
			new ReviewBody(
				review.getReviewId(),
				review.getRating(),
				review.getContent()
			),
			new InterviewBody(
				form.getInterviewId(),
				form.getTitle(),
				form.getContent(),
				form.getInterviewMethod(),
				form.getPreferredDatetime(),
				form.getExtraDescription(),
				form.getMajorMessage(),
				form.getStatus()
			),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
