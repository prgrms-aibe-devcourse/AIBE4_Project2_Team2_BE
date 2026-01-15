package kr.java.aibe4_project2_team2_be.majormate.domain.qna.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	// 학생: 내 질문 목록(전공자 + 답변 필요)
	@EntityGraph(attributePaths = {"major", "answer"})
	Page<Question> findByStudent_MemberId(Long studentMemberId, Pageable pageable);

	// 전공자/공개: 전공자에게 달린 질문 목록(학생 + 답변 필요)
	@EntityGraph(attributePaths = {"student", "answer"})
	Page<Question> findByMajor_MemberId(Long majorMemberId, Pageable pageable);

    // 관리자: 전체 질문 목록 조회 (학생, 전공자 정보 함께 조회)
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"student", "major", "answer"})
    Page<Question> findAll(@NonNull Pageable pageable);
}

