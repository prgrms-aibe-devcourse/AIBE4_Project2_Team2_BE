package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record PublicQnaResponse(
	StudentInfo student,
	QuestionBody question,
	AnswerBody answer
) {
	public record StudentInfo(
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
	}

	public record QuestionBody(
		String content,
		LocalDateTime createdAt
	) {
	}

	public record AnswerBody(
		String content,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
	}

	public static PublicQnaResponse from(Question question) {
		MemberProfile student = question.getStudent();
		Answer answer = question.getAnswer();

		return new PublicQnaResponse(
			new StudentInfo(
				student.getProfileImageUrl(),
				student.getNickname(),
				student.getStatus(),
				student.getAcademic().getUniversity(),
				student.getAcademic().getMajor()
			),
			new QuestionBody(
				question.getContent(),
				question.getCreatedAt()
			),
			answer == null ? null : new AnswerBody(
				answer.getContent(), answer.getCreatedAt(), answer.getUpdatedAt()
			)
		);
	}
}
