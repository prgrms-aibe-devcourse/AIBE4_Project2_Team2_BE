package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response;

import java.time.LocalDateTime;
import java.util.Objects;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record ReceivedInterviewFormResponse(
	StudentInfo student,
	InterviewFormBody interview,
	InterviewFormStatus status,
	String majorMessage,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record StudentInfo(
		Long studentMemberId,
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
		String content, // 목록 응답에서는 null
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription
	) {
	}

	public static ReceivedInterviewFormResponse fromSummary(InterviewStudentSnapshot student,
		InterviewForm interviewForm) {
		Objects.requireNonNull(student, "student snapshot must not be null");
		Objects.requireNonNull(interviewForm, "interviewForm must not be null");

		return new ReceivedInterviewFormResponse(
			toStudentInfo(student, interviewForm),
			new InterviewFormBody(
				interviewForm.getInterviewId(),
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

	public static ReceivedInterviewFormResponse fromDetail(InterviewStudentSnapshot student,
		InterviewForm interviewForm) {
		Objects.requireNonNull(student, "student snapshot must not be null");
		Objects.requireNonNull(interviewForm, "interviewForm must not be null");

		return new ReceivedInterviewFormResponse(
			toStudentInfo(student, interviewForm),
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

	private static StudentInfo toStudentInfo(InterviewStudentSnapshot student, InterviewForm interviewForm) {
		return new StudentInfo(
			interviewForm.getStudentMemberId(),
			student.getProfileImageUrl(),
			student.getNickname(),
			student.getStatus(),
			student.getUniversity(),
			student.getMajor()
		);
	}
}
