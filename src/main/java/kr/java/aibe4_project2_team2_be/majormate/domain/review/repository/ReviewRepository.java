package kr.java.aibe4_project2_team2_be.majormate.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByInterviewIdInOrderByCreatedAtDesc(List<Long> interviewIds);

	boolean existsByInterviewId(Long interviewId);
}
