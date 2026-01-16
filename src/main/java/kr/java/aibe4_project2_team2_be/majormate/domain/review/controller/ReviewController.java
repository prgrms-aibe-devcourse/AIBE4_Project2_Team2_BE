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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.request.ReviewRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.PublicReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.response.ReviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.review.service.ReviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ErrorResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageMeta;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageResponses;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "후기", description = "후기 목록/상세 조회 및 작성/수정 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * /api/members/me/reviews?type=WRITTEN
	 * /api/members/me/reviews?type=RECEIVED
	 **/
	@Operation(
		summary = "내 후기 목록 조회",
		description = "로그인 사용자의 후기 목록을 조회한다. type(작성/받음)에 따라 조회 범위가 달라진다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyReviewsSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(type 누락/값 오류 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 오류",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@GetMapping("/members/me/reviews")
	public ApiResponse<List<ReviewResponse>> getMyReviews(
		@Parameter(description = "조회 타입(WRITTEN: 내가 작성한 후기, RECEIVED: 내가 받은 후기)", required = true)
		@RequestParam(name = "type") ReviewResponse.ViewType type,

		@Parameter(description = "정렬 기준(기본값: CREATED_AT_DESC)", required = false)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,

		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<ReviewResponse> page = reviewService.getMyReviews(memberId, type, sort, pageable);
		return PageResponses.of(page);
	}

	@Operation(
		summary = "내 후기 상세 조회",
		description = "로그인 사용자가 작성했거나(WRITTEN) 받았던(RECEIVED) 후기 상세를 조회한다. 단일 엔드포인트로 처리한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyReviewDetailSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "접근 권한 없음(본인이 작성/수신한 후기가 아님)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "후기 정보를 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@GetMapping("/members/me/reviews/{reviewId}")
	public ApiResponse<ReviewResponse> getMyReviewDetail(
		@Parameter(description = "후기 ID", required = true)
		@PathVariable Long reviewId
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.getMyReviewDetail(memberId, reviewId);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "전공자 공개 후기 목록 조회",
		description = "특정 전공자(majorId)가 받은 후기 목록을 공개 범위로 조회한다. type은 현재 RECEIVED만 허용하며 그 외 값이면 COMMON_400으로 처리한다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMajorPublicReviewsSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(type이 RECEIVED가 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 오류",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@GetMapping("/majors/{majorId}/reviews")
	public ApiResponse<List<PublicReviewResponse>> getMajorPublicReviews(
		@Parameter(description = "전공자 memberId", required = true)
		@PathVariable Long majorId,

		@Parameter(description = "조회 타입(현재 RECEIVED만 허용, 기본값: RECEIVED)", required = false)
		@RequestParam(name = "type", required = false, defaultValue = "RECEIVED") String type,

		@Parameter(description = "정렬 기준(기본값: CREATED_AT_DESC)", required = false)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,

		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		if (!"RECEIVED".equalsIgnoreCase(type)) {
			throw new BusinessException(ErrorCode.COMMON_400);
		}
		Page<PublicReviewResponse> page =
			reviewService.getMajorPublicReceivedReviews(majorId, sort, pageable);
		return PageResponses.of(page);
	}

	@Operation(
		summary = "후기 작성",
		description = "특정 인터뷰(interviewId)에 대한 후기를 작성한다. 완료된 인터뷰에 대해서만 작성 가능하며 동일 인터뷰에 대한 후기 중복 작성은 허용되지 않는다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = CreateReviewSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패, 완료된 인터뷰가 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "권한 없음(작성 권한이 없음)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409",
			description = "충돌(이미 해당 인터뷰에 대한 후기가 존재)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "후기/인터뷰 정보를 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "후기 작성 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ReviewRequest.class)
		)
	)
	@PostMapping("/interviews/{interviewId}/reviews")
	public ApiResponse<ReviewResponse> createReview(
		@Parameter(description = "인터뷰 ID", required = true)
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.createReview(memberId, interviewId, request);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "후기 수정",
		description = "특정 인터뷰(interviewId)에 대한 후기를 수정한다. 작성자만 수정할 수 있으며 정책에 따라 수정이 제한될 수 있다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateReviewSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "401",
			description = "인증 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "403",
			description = "권한 없음(작성자가 아님)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "후기/인터뷰 정보를 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "후기 수정 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ReviewRequest.class)
		)
	)
	@PatchMapping("/interviews/{interviewId}/reviews")
	public ApiResponse<ReviewResponse> updateReview(
		@Parameter(description = "인터뷰 ID", required = true)
		@PathVariable Long interviewId,
		@Valid @RequestBody ReviewRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		ReviewResponse response = reviewService.updateReview(memberId, interviewId, request);
		return ApiResponse.success(response);
	}

	/*
	  Swagger 문서 전용 스키마
	  - 실제 런타임 응답 타입을 변경하지 않기 위해, 문서용 wrapper 클래스를 둔다
	*/

	@Schema(
		name = "ApiResponseReviewResponseListWithPageMeta",
		description = "성공 응답(ApiResponse) - data에 ReviewResponse 리스트, meta에 PageMeta 포함"
	)
	static class GetMyReviewsSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = ReviewResponse.class))
		public List<ReviewResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseReviewResponse_GetDetail",
		description = "성공 응답(ApiResponse) - data에 ReviewResponse 포함"
	)
	static class GetMyReviewDetailSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = ReviewResponse.class)
		public ReviewResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponsePublicReviewResponseListWithPageMeta",
		description = "성공 응답(ApiResponse) - data에 PublicReviewResponse 리스트, meta에 PageMeta 포함"
	)
	static class GetMajorPublicReviewsSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = PublicReviewResponse.class))
		public List<PublicReviewResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseReviewResponse_Create",
		description = "성공 응답(ApiResponse) - 후기 작성 결과"
	)
	static class CreateReviewSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = ReviewResponse.class)
		public ReviewResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseReviewResponse_Update",
		description = "성공 응답(ApiResponse) - 후기 수정 결과"
	)
	static class UpdateReviewSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = ReviewResponse.class)
		public ReviewResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(name = "ApiResponseErrorResponse", description = "에러 응답(ApiResponse) - error에 ErrorResponse 포함")
	static class ErrorDoc {
		@Schema(example = "false")
		public boolean success;

		@Schema(nullable = true)
		public Object data;

		@Schema(nullable = true)
		public Object meta;

		@Schema(implementation = ErrorResponse.class)
		public ErrorResponse error;
	}
}
