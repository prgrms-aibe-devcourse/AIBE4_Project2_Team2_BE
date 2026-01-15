package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminReviewDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminReviewDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller; // ★ 여기 변경
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // ★ RestController 아님!
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    // 1. 목록 화면
    @GetMapping
    public String reviewList(Model model,
                             @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminReviewDto> reviews = adminReviewService.findAllReviews(pageable);
        model.addAttribute("reviews", reviews);
        return "admin/review/review-list"; // HTML 파일 경로
    }

    // 2. 상세 화면
    @GetMapping("/{reviewId}") // URL을 깔끔하게 /detail 뺐습니다.
    public String reviewDetail(@PathVariable Long reviewId, Model model) {
        AdminReviewDetailDto review = adminReviewService.findReviewDetail(reviewId);
        model.addAttribute("review", review);
        return "admin/review/review-detail"; // HTML 파일 경로
    }

    // 3. 삭제 처리 (HTML 없음, 리다이렉트)
    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId) {
        adminReviewService.deleteReview(reviewId);
        return "redirect:/admin/reviews"; // 목록으로 강제 이동
    }
}