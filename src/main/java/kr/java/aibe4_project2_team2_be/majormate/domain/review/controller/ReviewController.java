package kr.java.aibe4_project2_team2_be.majormate.domain.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.ReviewCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.ReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.service.ReviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/members/me/reviews")
	public ApiResponse<List<ReviewResponse>> getMyReviews() {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 1L;
		List<ReviewResponse> response = reviewService.getReviews(memberId);
		return ApiResponse.success(response);
	}

	@PostMapping("/interviews/{interviewId}/reviews")
	public ResponseEntity<Void> createReview(
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewCreateRequest request
	) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 2L;
		reviewService.createReview(memberId, interviewId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
