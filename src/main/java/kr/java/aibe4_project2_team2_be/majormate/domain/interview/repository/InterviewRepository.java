package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.Interview;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewStatus;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

	@Query("select i.interviewId from Interview i where i.studentMemberId = :studentId")
	List<Long> findInterviewIdsByStudentMemberId(@Param("studentId") Long studentId);

	List<Interview> findByStudentMemberIdOrderByCreatedAtDesc(Long studentMemberId);

	boolean existsByStudentMemberIdAndMajorMemberIdAndStatusNot(
		Long studentMemberId,
		Long majorMemberId,
		InterviewStatus status
	);
}
