package kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

	boolean existsByQuestion_QuestionId(Long questionId);

	@EntityGraph(attributePaths = {"question", "question.student"})
	Page<Answer> findByQuestion_Major_MemberId(Long majorMemberId, Pageable pageable);
}

