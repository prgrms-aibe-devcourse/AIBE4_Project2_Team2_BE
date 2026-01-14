package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record InterviewFormResponse(
	Long interviewId,
	ViewType viewType,
	PeerInfo peer,
	InterviewBody interview,
	InterviewFormStatus status,
	String majorMessage,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public enum ViewType {
		APPLIED, RECEIVED
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

	public record InterviewBody(
		String title,
		String content,
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription
	) {
	}

	public static InterviewFormResponse appliedSummary(InterviewMajorSnapshot major, InterviewForm form) {
		return new InterviewFormResponse(
			form.getInterviewId(),
			ViewType.APPLIED,
			new PeerInfo(
				form.getMajorMemberId(),
				major.getProfileImageUrl(),
				major.getNickname(),
				major.getStatus(),
				major.getUniversity(),
				major.getMajor()
			),
			new InterviewBody(
				form.getTitle(),
				null,
				null,
				null,
				null
			),
			form.getStatus(),
			null,
			form.getCreatedAt(),
			form.getUpdatedAt()
		);
	}

	public static InterviewFormResponse receivedSummary(InterviewStudentSnapshot student, InterviewForm form) {
		return new InterviewFormResponse(
			form.getInterviewId(),
			ViewType.RECEIVED,
			new PeerInfo(
				form.getStudentMemberId(),
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
			),
			new InterviewBody(
				form.getTitle(),
				null,
				null,
				form.getPreferredDatetime(),
				null
			),
			form.getStatus(),
			null,
			form.getCreatedAt(),
			form.getUpdatedAt()
		);
	}

	public static InterviewFormResponse appliedDetail(InterviewMajorSnapshot major, InterviewForm form) {
		return new InterviewFormResponse(
			form.getInterviewId(),
			ViewType.APPLIED,
			new PeerInfo(
				form.getMajorMemberId(),
				major.getProfileImageUrl(),
				major.getNickname(),
				major.getStatus(),
				major.getUniversity(),
				major.getMajor()
			),
			new InterviewBody(
				form.getTitle(),
				form.getContent(),
				form.getInterviewMethod(),
				form.getPreferredDatetime(),
				form.getExtraDescription()
			),
			form.getStatus(),
			form.getMajorMessage(),
			form.getCreatedAt(),
			form.getUpdatedAt()
		);
	}

	public static InterviewFormResponse receivedDetail(InterviewStudentSnapshot student, InterviewForm form) {
		return new InterviewFormResponse(
			form.getInterviewId(),
			ViewType.RECEIVED,
			new PeerInfo(
				form.getStudentMemberId(),
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
			),
			new InterviewBody(
				form.getTitle(),
				form.getContent(),
				form.getInterviewMethod(),
				form.getPreferredDatetime(),
				form.getExtraDescription()
			),
			form.getStatus(),
			form.getMajorMessage(),
			form.getCreatedAt(),
			form.getUpdatedAt()
		);
	}
}
