package kr.java.aibe4_project2_team2_be.majormate.domain.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.request.ReviewRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.ReceivedReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.WrittenReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.service.ReviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/members/me/reviews/written")
	public ApiResponseNew<java.util.List<WrittenReviewResponse>> getMyWrittenReviews(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<WrittenReviewResponse> page = reviewService.getWrittenReviews(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/reviews/written/{reviewId}")
	public ApiResponseNew<WrittenReviewResponse> getMyWrittenReviewDetail(@PathVariable Long reviewId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		WrittenReviewResponse response = reviewService.getWrittenReviewDetail(memberId, reviewId);
		return ApiResponseNew.success(response);
	}

	@GetMapping("/members/me/reviews/received")
	public ApiResponseNew<List<ReceivedReviewResponse>> getMyReceivedReviews(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<ReceivedReviewResponse> page = reviewService.getReceivedReviews(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/reviews/received/{reviewId}")
	public ApiResponseNew<ReceivedReviewResponse> getMyReceivedReviewDetail(@PathVariable Long reviewId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReceivedReviewResponse response = reviewService.getReceivedReviewDetail(memberId, reviewId);
		return ApiResponseNew.success(response);
	}

	@PostMapping("/interviews/{interviewId}/reviews")
	public ApiResponseNew<WrittenReviewResponse> createReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		WrittenReviewResponse response = reviewService.createReview(memberId, interviewId, request);
		return ApiResponseNew.success(response);
	}

	@PatchMapping("/interviews/{interviewId}/reviews")
	public ApiResponseNew<WrittenReviewResponse> updateReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		WrittenReviewResponse response = reviewService.updateReview(memberId, interviewId, request);
		return ApiResponseNew.success(response);
	}
}
