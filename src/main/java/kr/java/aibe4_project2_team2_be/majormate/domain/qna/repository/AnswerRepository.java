package kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

	// 질문 1개당 답변 1개 전제
	Optional<Answer> findByQuestion_QuestionId(Long questionId);

	boolean existsByQuestion_QuestionId(Long questionId);

	// 전공자: 내가 작성한 답변 목록(질문도 같이 조회)
	// 응답에 questionContent, studentMemberId가 들어가므로 question, question.student를 함께 로딩
	@EntityGraph(attributePaths = {"question", "question.student"})
	Page<Answer> findByQuestion_Major_MemberId(Long majorMemberId, Pageable pageable);

	// 전공자: 답변 수정 권한 체크용(본인 답변만)
	Optional<Answer> findByAnswerIdAndQuestion_Major_MemberId(Long answerId, Long majorMemberId);
}
