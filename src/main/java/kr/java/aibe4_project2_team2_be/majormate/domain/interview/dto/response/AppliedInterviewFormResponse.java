package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record AppliedInterviewFormResponse(
	Long interviewId,
	MajorInfo major,
	InterviewFormBody interview,
	InterviewFormStatus status,
	String majorMessage,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record MajorInfo(
		Long majorMemberId,
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
	}

	public record InterviewFormBody(
		String title,
		String content, // 목록 응답에서는 null
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription
	) {
	}

	public static AppliedInterviewFormResponse fromSummary(InterviewMajorSnapshot major, InterviewForm interviewForm) {
		Objects.requireNonNull(major, "major snapshot must not be null");
		Objects.requireNonNull(interviewForm, "interviewForm must not be null");

		return new AppliedInterviewFormResponse(
			interviewForm.getInterviewId(),
			toMajorInfo(major, interviewForm),
			new InterviewFormBody(
				interviewForm.getTitle(),
				null,
				interviewForm.getInterviewMethod(),
				interviewForm.getPreferredDatetime(),
				interviewForm.getExtraDescription()
			),
			interviewForm.getStatus(),
			interviewForm.getMajorMessage(),
			interviewForm.getCreatedAt(),
			interviewForm.getUpdatedAt()
		);
	}

	public static AppliedInterviewFormResponse fromDetail(InterviewMajorSnapshot major, InterviewForm interviewForm) {
		Objects.requireNonNull(major, "major snapshot must not be null");
		Objects.requireNonNull(interviewForm, "interviewForm must not be null");

		return new AppliedInterviewFormResponse(
			interviewForm.getInterviewId(),
			toMajorInfo(major, interviewForm),
			new InterviewFormBody(
				interviewForm.getTitle(),
				interviewForm.getContent(),
				interviewForm.getInterviewMethod(),
				interviewForm.getPreferredDatetime(),
				interviewForm.getExtraDescription()
			),
			interviewForm.getStatus(),
			interviewForm.getMajorMessage(),
			interviewForm.getCreatedAt(),
			interviewForm.getUpdatedAt()
		);
	}

	private static MajorInfo toMajorInfo(InterviewMajorSnapshot major, InterviewForm interviewForm) {
		return new MajorInfo(
			interviewForm.getMajorMemberId(),
			major.getProfileImageUrl(),
			major.getNickname(),
			major.getStatus(),
			major.getUniversity(),
			major.getMajor()
		);
	}
}
