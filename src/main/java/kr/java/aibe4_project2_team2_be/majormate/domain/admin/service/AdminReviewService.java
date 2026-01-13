package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 목록 조회 (검색 + 페이징)
    public Page<Review> getReviews(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return reviewRepository.findByContentContaining(keyword, pageable);
        }
        return reviewRepository.findAll(pageable);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        reviewRepository.delete(review);
    }
}