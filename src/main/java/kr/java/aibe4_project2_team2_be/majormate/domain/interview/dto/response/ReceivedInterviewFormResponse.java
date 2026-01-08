package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record ReceivedInterviewFormResponse(
	StudentInfo student,
	InterviewFormBody interview,
	InterviewFormStatus status,
	String majorMessage
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
		String content,
		String interviewMethod,
		LocalDateTime preferredDatetime,
		String extraDescription
	) {
	}

	public static ReceivedInterviewFormResponse from(InterviewStudentSnapshot student, InterviewForm interviewForm) {
		return new ReceivedInterviewFormResponse(
			new StudentInfo(
				interviewForm.getStudentMemberId(),
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getUniversity(),
				student.getMajor()
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
			interviewForm.getMajorMessage()
		);
	}
}
