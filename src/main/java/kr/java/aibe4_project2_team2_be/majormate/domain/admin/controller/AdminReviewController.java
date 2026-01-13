package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminReviewService;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.entity.Review;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    // 1. 리뷰 목록 조회
    @GetMapping
    public String reviewList(Model model,
                             @RequestParam(required = false) String keyword,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Review> reviewPage = adminReviewService.getReviews(keyword, pageable);

        // Entity -> DTO 변환하여 View로 전달
        model.addAttribute("reviews", reviewPage.map(AdminReviewDto::new));
        model.addAttribute("keyword", keyword);

        return "admin/content/review-list";
    }

    // 2. 리뷰 삭제
    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminReviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }

    // 목록 보여주기용 DTO
    @Getter
    static class AdminReviewDto {
        private Long id;
        private Long interviewId; // 인터뷰 연결 정보
        private int rating;
        private String content;
        private LocalDateTime createdAt;

        public AdminReviewDto(Review review) {
            this.id = review.getReviewId();
            this.interviewId = review.getInterviewId();
            this.rating = review.getRating();
            this.content = review.getContent();
            this.createdAt = review.getCreatedAt();
        }
    }
}