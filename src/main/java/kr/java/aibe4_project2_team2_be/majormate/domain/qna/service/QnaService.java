package kr.java.aibe4_project2_team2_be.majormate.domain.qna.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.request.QnaRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.IdResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.MyAnswerItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QuestionMyItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QuestionReceivedItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.AnswerRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	private final MemberInfoReader memberInfoReader;

	public Page<QuestionMyItemResponse> getMyQuestions(Long studentMemberId, Pageable pageable) {
		Page<Question> page = questionRepository.findByStudent_MemberId(studentMemberId, pageable);

		List<Question> questions = page.getContent();
		if (questions.isEmpty()) {
			return Page.empty(pageable);
		}

		List<QuestionMyItemResponse> content = questions.stream()
			.map(q -> new QuestionMyItemResponse(
				q.getQuestionId(),
				q.getMajor().getMemberId(),
				q.getContent(),
				q.isHasAnswer(),
				q.getAnswer() == null ? null : new QuestionMyItemResponse.AnswerSummary(
					q.getAnswer().getAnswerId(),
					q.getAnswer().getContent(),
					q.getAnswer().getCreatedAt(),
					q.getAnswer().getUpdatedAt()
				),
				q.getCreatedAt(),
				q.getUpdatedAt()
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	public Page<QuestionReceivedItemResponse> getReceivedQuestions(Long majorMemberId, Pageable pageable) {
		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Page<Question> questionPage = questionRepository.findByMajor_MemberId(majorMemberId, pageable);

		return questionPage.map(q -> new QuestionReceivedItemResponse(
			q.getQuestionId(),
			q.getStudent().getMemberId(),
			q.getStudent().getNickname(),
			q.getContent(),
			q.isHasAnswer(),
			q.getAnswer() != null ? q.getAnswer().getContent() : null,      // 답변 내용
			q.getAnswer() != null ? q.getAnswer().getCreatedAt() : null,    // 답변 생성일
			q.getCreatedAt()
		));
	}

	public Page<MyAnswerItemResponse> getMyAnswers(Long majorMemberId, Pageable pageable) {
		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Page<Answer> page = answerRepository.findByQuestion_Major_MemberId(majorMemberId, pageable);

		List<Answer> answers = page.getContent();
		if (answers.isEmpty()) {
			return Page.empty(pageable);
		}

		List<MyAnswerItemResponse> content = answers.stream()
			.map(a -> new MyAnswerItemResponse(
				a.getAnswerId(),
				a.getQuestion().getQuestionId(),
				a.getQuestion().getStudent().getMemberId(),
				a.getQuestion().getContent(),
				a.getContent(),
				a.getCreatedAt(),
				a.getUpdatedAt()
			))
			.toList();

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	@Transactional
	public IdResponse createQuestion(Long studentMemberId, Long majorId, QnaRequest request) {
		validateCreateQuestionRequestOrThrow(studentMemberId, majorId, request);

		MemberProfile studentProfile = memberInfoReader.getProfileOrThrow(studentMemberId);
		MemberProfile majorProfile = memberInfoReader.getProfileOrThrow(majorId);

		validateQuestionRuleOrThrow(studentProfile, majorProfile);

		Question saved = questionRepository.save(
			Question.create(
				memberInfoReader.getProfileOrThrow(studentMemberId),
				memberInfoReader.getProfileOrThrow(majorId),
				request.content()
			)
		);

		return new IdResponse(saved.getQuestionId());
	}

	@Transactional
	public IdResponse createAnswer(Long majorMemberId, Long questionId, QnaRequest request) {
		if (majorMemberId == null || questionId == null || request == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

		validateQuestionTargetMajorOrThrow(question, majorMemberId);
		validateQuestionNotAnsweredOrThrow(question);

		boolean exists = answerRepository.existsByQuestion_QuestionId(questionId);
		if (exists) {
			throw new BusinessException(ErrorCode.QNA_409_ANSWER_ALREADY_EXISTS);
		}

		Answer saved = answerRepository.save(Answer.create(question, request.content()));
		question.attachAnswer(saved);

		return new IdResponse(saved.getAnswerId());
	}

	@Transactional
	public void updateMyQuestion(Long studentMemberId, Long questionId, QnaRequest request) {
		if (studentMemberId == null || questionId == null || request == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

		validateQuestionOwnerOrThrow(question, studentMemberId);
		validateQuestionNotAnsweredOrThrow(question);

		question.updateContent(request.content());
	}

	@Transactional
	public void deleteMyQuestion(Long studentMemberId, Long questionId) {
		if (studentMemberId == null || questionId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

		validateQuestionOwnerOrThrow(question, studentMemberId);
		validateQuestionNotAnsweredOrThrow(question);

		questionRepository.delete(question);
	}

	@Transactional
	public void updateMyAnswer(Long majorMemberId, Long answerId, QnaRequest request) {
		if (majorMemberId == null || answerId == null || request == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Answer answer = answerRepository.findById(answerId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_ANSWER));

		validateAnswerOwnerOrThrow(answer, majorMemberId);

		answer.updateContent(request.content());
	}

	private void validateCreateQuestionRequestOrThrow(Long studentMemberId, Long majorId,
		QnaRequest request) {
		if (studentMemberId == null || majorId == null || request == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (Objects.equals(studentMemberId, majorId)) {
			throw new BusinessException(ErrorCode.QNA_400_SELF_QUESTION_NOT_ALLOWED);
		}
		if (request.content() == null || request.content().isBlank()) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
	}

	private void validateQuestionRuleOrThrow(MemberProfile studentProfile, MemberProfile majorProfile) {
		if (studentProfile.getRole() == MemberRole.ADMIN) {
			throw new BusinessException(ErrorCode.COMMON_403);
		}
		if (majorProfile.getRole() != MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.QNA_400_TARGET_NOT_MAJOR);
		}
	}

	private void validateQuestionOwnerOrThrow(Question question, Long studentMemberId) {
		if (!Objects.equals(question.getStudent().getMemberId(), studentMemberId)) {
			throw new BusinessException(ErrorCode.COMMON_403);
		}
	}

	private void validateQuestionTargetMajorOrThrow(Question question, Long majorMemberId) {
		if (!Objects.equals(question.getMajor().getMemberId(), majorMemberId)) {
			throw new BusinessException(ErrorCode.COMMON_403);
		}
	}

	private void validateAnswerOwnerOrThrow(Answer answer, Long majorMemberId) {
		if (!Objects.equals(answer.getQuestion().getMajor().getMemberId(), majorMemberId)) {
			throw new BusinessException(ErrorCode.COMMON_403);
		}
	}

	private void validateQuestionNotAnsweredOrThrow(Question question) {
		if (question.isHasAnswer()) {
			throw new BusinessException(ErrorCode.QNA_400_ALREADY_ANSWERED);
		}
	}
}
