package kr.java.aibe4_project2_team2_be.majormate.domain.qna.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.AnswerRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionAnswerService {

	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	public Page<QuestionAnswerResponse> getQuestions(Long memberId, Pageable pageable) {
		Page<Question> page = questionRepository.findByStudentMemberId(memberId, pageable);

		List<Question> questions = page.getContent();
		if (questions.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> questionIds = extractInterviewIds(questions);

		Map<Long, Answer> answerByQuestionId = answerRepository.findByQuestionIdIn(questionIds).stream()
			.collect(Collectors.toMap(a -> a.getQuestion().getQuestionId(), Function.identity(), (a, b) -> a));

		List<QuestionAnswerResponse> items = questions.stream()
			.map(q -> toResponse(q, hasAnswerByQuestionId.getOrDefault(q.getQuestionId(), false)))
			.toList();

		return new PageImpl<>(items, pageable, page.getTotalElements());

		return new PageImpl<>(questions, pageable, page.getTotalElements());
	}

	private List<Long> extractInterviewIds(List<Question> contents) {
		return contents.stream()
			.map(Question::getQuestionId)
			.distinct()
			.toList();
	}
}
