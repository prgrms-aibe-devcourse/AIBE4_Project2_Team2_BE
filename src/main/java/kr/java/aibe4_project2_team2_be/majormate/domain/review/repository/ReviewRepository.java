package kr.java.aibe4_project2_team2_be.majormate.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. 전체 조회 (페이징)
    Page<Review> findAll(Pageable pageable); // 형민

    // 2. 내용 검색 (페이징) - LIKE %keyword%
    Page<Review> findByContentContaining(String keyword, Pageable pageable); // 형민


	Optional<Review> findByInterviewId(Long interviewId);

	boolean existsByInterviewId(Long interviewId);

	@Query("""
			select r
			from Review r
			join InterviewForm f on f.interviewId = r.interviewId
			where f.studentMemberId = :studentId
			order by r.createdAt desc
		""")
	Page<Review> findWrittenByStudent(@Param("studentId") Long studentId, Pageable pageable);

	@Query("""
			select r
			from Review r
			join InterviewForm f on f.interviewId = r.interviewId
			where f.majorMemberId = :majorId
			order by r.createdAt desc
		""")
	Page<Review> findReceivedByMajor(@Param("majorId") Long majorId, Pageable pageable);
}
