package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewForm;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;

public interface InterviewFormRepository extends JpaRepository<InterviewForm, Long> {

	Page<InterviewForm> findByStudentMemberId(Long studentMemberId, Pageable pageable);

	Page<InterviewForm> findByMajorMemberId(Long majorMemberId, Pageable pageable);

	Page<InterviewForm> findByStudentMemberIdAndStatus(
		Long studentMemberId, InterviewFormStatus status, Pageable pageable
	);

	boolean existsByStudentMemberIdAndMajorMemberIdAndStatusIn(
		Long studentMemberId, Long majorMemberId, List<InterviewFormStatus> statuses
	);
}
