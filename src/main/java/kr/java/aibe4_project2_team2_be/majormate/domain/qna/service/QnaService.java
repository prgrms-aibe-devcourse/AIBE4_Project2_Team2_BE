package kr.java.aibe4_project2_team2_be.majormate.domain.qna.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.request.QnaRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.IdResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.PublicQnaResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QnaResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.AnswerRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaService {

	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final MemberInfoReader memberInfoReader;

	@Transactional(readOnly = true)
	public Page<QnaResponse> getMyQuestions(Long studentMemberId, PageSort sort, Pageable pageable) {
		if (studentMemberId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		Pageable p = toPageable(pageable, sort);
		Page<Question> page = questionRepository.findByStudent_MemberId(studentMemberId, p);
		if (page.isEmpty()) {
			return Page.empty(p);
		}

		List<QnaResponse> content = page.getContent().stream()
			.map(q -> QnaResponse.forStudent(q, q.getMajor()))
			.toList();

		return new PageImpl<>(content, p, page.getTotalElements());
	}

	@Transactional(readOnly = true)
	public Page<QnaResponse> getMyAnswers(Long majorMemberId, PageSort sort, Pageable pageable) {
		if (majorMemberId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Pageable p = toPageable(pageable, sort);

		Page<Answer> page = answerRepository.findByQuestion_Major_MemberId(majorMemberId, p);
		if (page.isEmpty()) {
			return Page.empty(p);
		}

		List<QnaResponse> content = page.getContent().stream()
			.map(a -> {
				Question q = a.getQuestion();
				return QnaResponse.forMajor(q, q.getStudent());
			})
			.toList();

		return new PageImpl<>(content, p, page.getTotalElements());
	}

	@Transactional(readOnly = true)
	public Page<PublicQnaResponse> getMajorPublicQnA(Long majorId, PageSort sort, Pageable pageable) {
		if (majorId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		Pageable p = toPageable(pageable, sort);

		Page<Question> page = questionRepository.findByMajor_MemberId(majorId, p);
		if (page.isEmpty()) {
			return Page.empty(p);
		}

		List<PublicQnaResponse> content = page.getContent().stream()
			.map(PublicQnaResponse::from)
			.toList();

		return new PageImpl<>(content, p, page.getTotalElements());
	}

	@Transactional
	public IdResponse createQuestion(Long studentMemberId, Long majorId, QnaRequest request) {
		validateCreateQuestionRequestOrThrow(studentMemberId, majorId, request);

		MemberProfile studentProfile = memberInfoReader.getProfileOrThrow(studentMemberId);
		MemberProfile majorProfile = memberInfoReader.getProfileOrThrow(majorId);

		validateQuestionRuleOrThrow(studentProfile, majorProfile);

		Question saved = questionRepository.save(Question.create(studentProfile, majorProfile, request.content()));
		return new IdResponse(saved.getQuestionId());
	}

	@Transactional
	public IdResponse createAnswer(Long majorMemberId, Long questionId, QnaRequest request) {
		validateCreateAnswerRequestOrThrow(majorMemberId, questionId, request);

		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Question question = questionRepository.findById(questionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_QUESTION));

		validateQuestionTargetMajorOrThrow(question, majorMemberId);
		validateQuestionNotAnsweredOrThrow(question);

		if (answerRepository.existsByQuestion_QuestionId(questionId)) {
			throw new BusinessException(ErrorCode.QNA_409_ANSWER_ALREADY_EXISTS);
		}

		Answer saved = answerRepository.save(Answer.create(question, request.content()));
		return new IdResponse(saved.getAnswerId());
	}

	@Transactional
	public void updateMyAnswer(Long majorMemberId, Long answerId, QnaRequest request) {
		if (majorMemberId == null || answerId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (request == null) {
			throw new BusinessException(ErrorCode.QNA_400_QUESTION_REQUIRED);
		}
		if (request.content() == null || request.content().isBlank()) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}

		memberInfoReader.validateMajorRoleOrThrow(majorMemberId);

		Answer answer = answerRepository.findById(answerId)
			.orElseThrow(() -> new BusinessException(ErrorCode.QNA_404_ANSWER));

		validateAnswerOwnerOrThrow(answer, majorMemberId);
		answer.updateContent(request.content());
	}

	@Transactional
	public void updateMyQuestion(Long studentMemberId, Long questionId, QnaRequest request) {
		if (studentMemberId == null || questionId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (request == null) {
			throw new BusinessException(ErrorCode.QNA_400_QUESTION_REQUIRED);
		}
		if (request.content() == null || request.content().isBlank()) {
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

	private void validateCreateQuestionRequestOrThrow(Long studentMemberId, Long majorId, QnaRequest request) {
		if (studentMemberId == null || majorId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (request == null) {
			throw new BusinessException(ErrorCode.QNA_400_QUESTION_REQUIRED);
		}
		if (Objects.equals(studentMemberId, majorId)) {
			throw new BusinessException(ErrorCode.QNA_400_SELF_QUESTION_NOT_ALLOWED);
		}
		if (request.content() == null || request.content().isBlank()) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
	}

	private void validateCreateAnswerRequestOrThrow(Long majorMemberId, Long questionId, QnaRequest request) {
		if (majorMemberId == null || questionId == null) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		if (request == null) {
			throw new BusinessException(ErrorCode.QNA_400_QUESTION_REQUIRED);
		}
		if (request.content() == null || request.content().isBlank()) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
	}

	private void validateQuestionRuleOrThrow(MemberProfile studentProfile, MemberProfile majorProfile) {
		if (studentProfile.getRole()
			== kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole.ADMIN) {
			throw new BusinessException(ErrorCode.COMMON_403);
		}
		if (majorProfile.getRole()
			!= kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole.MAJOR) {
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
		if (question.isHasAnswer() || question.getAnswer() != null) {
			throw new BusinessException(ErrorCode.QNA_400_ALREADY_ANSWERED);
		}
	}

	private Pageable toPageable(Pageable pageable, PageSort sort) {
		int p = Math.max(0, pageable == null ? 0 : pageable.getPageNumber());
		int s = Math.max(1, pageable == null ? 10 : pageable.getPageSize());

		PageSort safeSort = sort == null ? PageSort.CREATED_AT_DESC : sort;

		Sort springSort = switch (safeSort) {
			case CREATED_AT_ASC -> Sort.by(Sort.Direction.ASC, "createdAt");
			case CREATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "createdAt");
		};

		return PageRequest.of(p, s, springSort);
	}
}
