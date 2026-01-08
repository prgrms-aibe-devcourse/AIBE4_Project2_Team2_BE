package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;

public interface InterviewFormRepository extends JpaRepository<InterviewForm, Long> {

	@Query("""
			select f.interviewId
			from InterviewForm f
			where f.studentMemberId = :studentId
		""")
	List<Long> findInterviewIdsByStudentMemberId(Long studentId);

	@Query("""
			select f.interviewId
			from InterviewForm f
			where f.majorMemberId = :majorId
		""")
	List<Long> findInterviewIdsByMajorMemberId(Long majorId);

	List<InterviewForm> findByStudentMemberIdOrderByCreatedAtDesc(Long studentId);

	List<InterviewForm> findByMajorMemberIdOrderByCreatedAtAsc(Long majorId);

	boolean existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
		Long studentMemberId,
		Long majorMemberId,
		List<InterviewFormStatus> statuses
	);
}
