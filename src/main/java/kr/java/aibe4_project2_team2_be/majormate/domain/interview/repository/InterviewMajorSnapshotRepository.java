package kr.java.aibe4_project2_team2_be.majormate.domain.interview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity.InterviewMajorSnapshot;

public interface InterviewMajorSnapshotRepository extends JpaRepository<InterviewMajorSnapshot, Long> {
}
