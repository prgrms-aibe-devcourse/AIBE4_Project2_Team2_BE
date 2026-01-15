package kr.java.aibe4_project2_team2_be.majormate.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findAll(Pageable pageable);

	Page<Review> findByContentContaining(String content, Pageable pageable);

	Optional<Review> findByInterviewId(Long interviewId);

	@Query("""
			select r
			from Review r
				join InterviewForm f on f.interviewId = r.interviewId
			where f.studentMemberId = :studentId
		""")
	Page<Review> findWrittenByStudent(@Param("studentId") Long studentId, Pageable pageable);

	@Query("""
			select r
			from Review r
				join InterviewForm f on f.interviewId = r.interviewId
			where f.majorMemberId = :majorId
		""")
	Page<Review> findReceivedByMajor(@Param("majorId") Long majorId, Pageable pageable);

	boolean existsByInterviewId(Long interviewId);


}
