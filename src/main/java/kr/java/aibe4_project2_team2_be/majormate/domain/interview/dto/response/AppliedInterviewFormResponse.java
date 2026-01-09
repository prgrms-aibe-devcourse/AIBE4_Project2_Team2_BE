package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record AppliedInterviewFormResponse(
	MajorInfo major,
	InterviewFormBody interview,
	InterviewFormStatus status,
	String majorMessage,
	LocalDateTime created_at,
	LocalDateTime updated_at
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
		Long interviewId,
		String title,
		String content,
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription
	) {
	}

	public static AppliedInterviewFormResponse from(InterviewMajorSnapshot major, InterviewForm interviewForm) {
		return new AppliedInterviewFormResponse(
			new MajorInfo(
				interviewForm.getMajorMemberId(),
				major.getProfileImageUrl(),
				major.getNickname(),
				major.getStatus(),
				major.getUniversity(),
				major.getMajor()
			),
			new InterviewFormBody(
				interviewForm.getInterviewId(),
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
}
