package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record ReceivedReviewResponse(
	StudentInfo student,
	ReviewBody review,
	InterviewBody interview, // 목록 null, 상세 값 채움
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record StudentInfo(
		Long studentMemberId,
		String profileImageUrl,
		String nickname,
		String university,
		String major,
		MemberStatus status
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
		InterviewFormStatus status
	) {
	}

	public static ReceivedReviewResponse fromSummary(
		Review review,
		InterviewForm form,
		InterviewStudentSnapshot studentSnapshot
	) {
		Objects.requireNonNull(review, "review must not be null");
		Objects.requireNonNull(form, "form must not be null");
		Objects.requireNonNull(studentSnapshot, "studentSnapshot must not be null");

		return new ReceivedReviewResponse(
			new StudentInfo(
				form.getStudentMemberId(),
				studentSnapshot.getProfileImageUrl(),
				studentSnapshot.getNickname(),
				studentSnapshot.getUniversity(),
				studentSnapshot.getMajor(),
				studentSnapshot.getStatus()
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

	public static ReceivedReviewResponse fromDetail(
		Review review,
		InterviewForm form,
		InterviewStudentSnapshot studentSnapshot
	) {
		ReceivedReviewResponse summary = fromSummary(review, form, studentSnapshot);

		return new ReceivedReviewResponse(
			summary.student(),
			summary.review(),
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
			summary.createdAt(),
			summary.updatedAt()
		);
	}
}
