package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;

public record QnaResponse(
	ViewType viewType,
	PeerInfo peer,
	QuestionBody question,
	AnswerBody answer, // hasAnswer=false면 null
	boolean hasAnswer
) {
	public enum ViewType {
		STUDENT, MAJOR
	}

	public record PeerInfo(
		Long memberId,
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
		public static PeerInfo from(MemberProfile profile) {
			return new PeerInfo(
				profile.getMemberId(),
				profile.getProfileImageUrl(),
				profile.getNickname(),
				profile.getStatus(),
				profile.getAcademic().getUniversity(),
				profile.getAcademic().getMajor()
			);
		}
	}

	public record QuestionBody(
		Long questionId,
		String content,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		public static QuestionBody from(Question question) {
			return new QuestionBody(
				question.getQuestionId(),
				question.getContent(),
				question.getCreatedAt(),
				question.getUpdatedAt()
			);
		}
	}

	public record AnswerBody(
		Long answerId,
		String content,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		public static AnswerBody from(Answer answer) {
			return new AnswerBody(
				answer.getAnswerId(),
				answer.getContent(),
				answer.getCreatedAt(),
				answer.getUpdatedAt()
			);
		}
	}

	public static QnaResponse forStudent(Question question, MemberProfile majorProfile) {
		AnswerBody answerBody = null;

		if (question.isHasAnswer()) {
			Answer answer = question.getAnswer();
			if (answer == null) {
				throw new BusinessException(ErrorCode.QNA_500_ANSWER_MISSING);
			}
			answerBody = AnswerBody.from(answer);
		}

		return new QnaResponse(
			ViewType.STUDENT,
			PeerInfo.from(majorProfile),
			QuestionBody.from(question),
			answerBody,
			question.isHasAnswer()
		);
	}

	public static QnaResponse forMajor(Question q, MemberProfile studentProfile) {
		AnswerBody answerBody = null;

		if (q.isHasAnswer()) {
			Answer a = q.getAnswer();
			if (a == null) {
				throw new BusinessException(ErrorCode.QNA_500_ANSWER_MISSING);
			}
			answerBody = AnswerBody.from(a);
		}

		return new QnaResponse(
			ViewType.MAJOR,
			PeerInfo.from(studentProfile),
			QuestionBody.from(q),
			answerBody,
			q.isHasAnswer()
		);
	}
}
