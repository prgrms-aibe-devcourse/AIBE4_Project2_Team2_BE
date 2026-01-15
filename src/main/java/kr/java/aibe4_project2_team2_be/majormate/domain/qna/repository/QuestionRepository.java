package kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	// 학생: 내가 작성한 질문 목록(답변 있으면 같이 조회)
	// N+1 방지: answer, major를 함께 로딩(majorId 접근 시 Lazy 이슈 방지)
	@EntityGraph(attributePaths = {"answer", "major"})
	Page<Question> findByStudent_MemberId(Long studentMemberId, Pageable pageable);

	// 전공자: 내가 받은 질문 목록
	// studentMemberId를 응답에 포함하므로 student를 같이 로딩(선택)
	@EntityGraph(attributePaths = {"student", "answer"})
	Page<Question> findByMajor_MemberId(Long majorMemberId, Pageable pageable);

	// 학생: 본인 질문 단건 권한 체크용
	Optional<Question> findByQuestionIdAndStudent_MemberId(Long questionId, Long studentMemberId);

	// 전공자: 본인에게 온 질문 단건 권한 체크용
	Optional<Question> findByQuestionIdAndMajor_MemberId(Long questionId, Long majorMemberId);

	// 존재 여부 체크(필요 시)
	boolean existsByQuestionIdAndStudent_MemberId(Long questionId, Long studentMemberId);

	boolean existsByQuestionIdAndMajor_MemberId(Long questionId, Long majorMemberId);
}
