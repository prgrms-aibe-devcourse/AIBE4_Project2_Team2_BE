package kr.java.aibe4_project2_team2_be.majormate.domain.review.controller;

import java.util.List;

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
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/members/me/reviews/written")
	public ApiResponse<List<WrittenReviewResponse>> getMyWrittenReviews() {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		List<WrittenReviewResponse> response = reviewService.getWrittenReviews(memberId);
		if (response.isEmpty()) {
			return ApiResponse.success(response, "작성한 후기가 없습니다.");
		}
		return ApiResponse.success(response);
	}

	@GetMapping("/members/me/reviews/received")
	public ApiResponse<List<ReceivedReviewResponse>> getMyReceivedReviews() {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long majorId = 15L;
		List<ReceivedReviewResponse> response = reviewService.getReceivedReviews(majorId);
		if (response.isEmpty()) {
			return ApiResponse.success(response, "작성된 후기가 없습니다.");
		}
		return ApiResponse.success(response);
	}

	@PostMapping("/interviews/{interviewId}/reviews")
	public ApiResponse<WrittenReviewResponse> createReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 3L;
		WrittenReviewResponse response = reviewService.createReview(studentId, interviewId, request);
		return ApiResponse.success(response);
	}

	@PatchMapping("/interviews/{interviewId}/reviews")
	public ApiResponse<WrittenReviewResponse> updateReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 3L;
		WrittenReviewResponse response = reviewService.updateReview(studentId, interviewId, request);
		return ApiResponse.success(response);
	}
}
