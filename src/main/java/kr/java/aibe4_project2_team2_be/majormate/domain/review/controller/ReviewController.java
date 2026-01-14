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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.request.ReviewRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.PublicReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.ReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.service.ReviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * /api/members/me/reviews?type=WRITTEN
	 * /api/members/me/reviews?type=RECEIVED
	 **/
	@GetMapping("/members/me/reviews")
	public ApiResponseNew<List<ReviewResponse>> getMyReviews(
		@RequestParam(name = "type") ReviewResponse.ViewType type,
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<ReviewResponse> page = reviewService.getMyReviews(memberId, type, sort, pageable);
		return PageResponsesNew.of(page);
	}

	// (내가 작성한/나에게 작성된) 후기 상세 조회를 한 엔드포인트로 처리한다
	@GetMapping("/members/me/reviews/{reviewId}")
	public ApiResponseNew<ReviewResponse> getMyReviewDetail(
		@PathVariable Long reviewId
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.getMyReviewDetail(memberId, reviewId);
		return ApiResponseNew.success(response);
	}

	@GetMapping("/majors/{majorId}/reviews")
	public ApiResponseNew<List<PublicReviewResponse>> getMajorPublicReviews(
		@PathVariable Long majorId,
		@RequestParam(name = "type", required = false, defaultValue = "RECEIVED") String type,
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		Pageable pageable
	) {
		if (!"RECEIVED".equalsIgnoreCase(type)) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		Page<PublicReviewResponse> page =
			reviewService.getMajorPublicReceivedReviews(majorId, sort, pageable);
		return PageResponsesNew.of(page);
	}

	@PostMapping("/interviews/{interviewId}/reviews")
	public ApiResponseNew<ReviewResponse> createReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.createReview(memberId, interviewId, request);
		return ApiResponseNew.success(response);
	}

	@PatchMapping("/interviews/{interviewId}/reviews")
	public ApiResponseNew<ReviewResponse> updateReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.updateReview(memberId, interviewId, request);
		return ApiResponseNew.success(response);
	}
}
