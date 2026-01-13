package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;

public interface InterviewFormRepository extends JpaRepository<InterviewForm, Long> {

	List<InterviewForm> findByInterviewIdIn(List<Long> interviewIds);

	Page<InterviewForm> findByStudentMemberId(Long studentMemberId, Pageable pageable);

	Page<InterviewForm> findByMajorMemberId(Long majorMemberId, Pageable pageable);

	Page<InterviewForm> findByStudentMemberIdAndStatus(
		Long studentMemberId, InterviewFormStatus status, Pageable pageable
	);

	@Query("""
		  select f
		  from InterviewForm f
		  left join Review r on r.interviewId = f.interviewId
		  where f.studentMemberId = :studentId
		    and f.status = :status
		    and r is null
		""")
	Page<InterviewForm> findCompletedWithoutReview(
		@Param("studentId") Long studentId,
		@Param("status") InterviewFormStatus status,
		Pageable pageable
	);

	boolean existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
		Long studentMemberId, Long majorMemberId, List<InterviewFormStatus> statuses
	);
}
