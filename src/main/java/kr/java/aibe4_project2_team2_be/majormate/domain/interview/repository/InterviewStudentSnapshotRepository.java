package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewStudentSnapshot;

public interface InterviewStudentSnapshotRepository extends JpaRepository<InterviewStudentSnapshot, Long> {

	List<InterviewStudentSnapshot> findByInterviewIdIn(List<Long> interviewIds);
}
