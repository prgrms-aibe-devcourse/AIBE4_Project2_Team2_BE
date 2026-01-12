package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;

public record WrittenReviewResponse(
	MajorInfo major,
	ReviewBody review,
	InterviewBody interview, // 목록에서는 null, 상세에서는 값 채움
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

	public record ReviewBody(
		Long reviewId,
		Long interviewId,
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
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		InterviewFormStatus status
	) {
	}

	public static WrittenReviewResponse fromSummary(
		Review review,
		InterviewForm form,
		InterviewMajorSnapshot majorSnapshot
	) {
		Objects.requireNonNull(review, "review must not be null");
		Objects.requireNonNull(form, "form must not be null");
		Objects.requireNonNull(majorSnapshot, "majorSnapshot must not be null");

		return new WrittenReviewResponse(
			new MajorInfo(
				form.getMajorMemberId(),
				majorSnapshot.getProfileImageUrl(),
				majorSnapshot.getNickname(),
				majorSnapshot.getUniversity(),
				majorSnapshot.getMajor()
			),
			new ReviewBody(
				review.getReviewId(),
				review.getInterviewId(),
				review.getRating(),
				review.getContent()
			),
			null,
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}

	public static WrittenReviewResponse fromDetail(
		Review review,
		InterviewForm form,
		InterviewMajorSnapshot majorSnapshot
	) {
		WrittenReviewResponse summary = fromSummary(review, form, majorSnapshot);

		return new WrittenReviewResponse(
			summary.major(),
			summary.review(),
			new InterviewBody(
				form.getInterviewId(),
				form.getTitle(),
				form.getContent(),
				form.getInterviewMethod(),
				form.getPreferredDatetime(),
				form.getExtraDescription(),
				form.getMajorMessage(),
				form.getCreatedAt(),
				form.getUpdatedAt(),
				form.getStatus()
			),
			summary.createdAt(),
			summary.updatedAt()
		);
	}
}
