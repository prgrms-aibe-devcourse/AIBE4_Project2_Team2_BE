package kr.java.aibe4_project2_team2_be.majormate.domain.interview.controller;

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
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewStatusUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.InterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.service.InterviewFormService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ErrorResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageMeta;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageResponses;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "인터뷰", description = "인터뷰 신청서 조회/상세/생성/상태 변경 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewFormController {

	private final InterviewFormService interviewFormService;

	/**
	 * /api/members/me/interviews?type=APPLIED
	 * /api/members/me/interviews?type=RECEIVED
	 * /api/members/me/interviews?type=APPLIED&status=COMPLETED&reviewed=false
	 **/
	@Operation(
		summary = "내 인터뷰 신청서 목록 조회",
		description = "로그인 사용자의 인터뷰 신청서 목록을 조회한다. type(신청/받음) 기준으로 조회하며 status, reviewed로 추가 필터링할 수 있다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyInterviewFormsSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(type 누락/값 오류, status 값 오류 등)",
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
	@GetMapping("/members/me/interviews")
	public ApiResponse<List<InterviewFormResponse>> getMyInterviewForms(
		@Parameter(
			description = "조회 타입(APPLIED: 내가 신청한 인터뷰, RECEIVED: 내가 받은 인터뷰)",
			required = true
		)
		@RequestParam(name = "type") InterviewFormResponse.ViewType type,

		@Parameter(description = "상태 필터(선택)")
		@RequestParam(name = "status", required = false) InterviewFormStatus status,

		@Parameter(description = "reviewed 필터(선택). 주로 type=APPLIED&status=COMPLETED 조합에서 후기 작성 여부를 필터링")
		@RequestParam(name = "reviewed", required = false) Boolean reviewed,

		@Parameter(
			description = "정렬 기준(기본값: CREATED_AT_DESC)",
			required = false
		)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,

		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		Page<InterviewFormResponse> response = interviewFormService.getMyInterviewForms(
			requesterId, type, status, reviewed, sort, pageable
		);
		return PageResponses.of(response);
	}

	@Operation(
		summary = "내 인터뷰 신청서 상세 조회",
		description = "로그인 사용자의 인터뷰 신청서 상세를 조회한다. interviewId는 인터뷰 신청서 식별자다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyInterviewFormDetailSuccessDoc.class)
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
			description = "접근 권한 없음(본인 소유/수신 건이 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "인터뷰 신청 정보를 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@GetMapping("/members/me/interviews/{interviewId}")
	public ApiResponse<InterviewFormResponse> getMyInterviewFormDetail(
		@Parameter(description = "인터뷰 신청서 ID", required = true)
		@PathVariable Long interviewId
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		InterviewFormResponse response = interviewFormService.getMyInterviewFormDetail(requesterId, interviewId);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "인터뷰 신청서 생성",
		description = "학생이 특정 전공자(majorId)에게 인터뷰 신청서를 생성한다. 유효성 검증 실패, 자기 자신에게 신청, 대상이 전공자가 아닌 경우, 진행 중인 신청이 이미 존재하는 경우 등은 에러로 처리된다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = CreateInterviewFormSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패/자기 자신에게 신청/대상이 전공자가 아님 등)",
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
			responseCode = "409",
			description = "충돌(진행 중인 인터뷰 신청이 이미 존재)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 오류(스냅샷 누락 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "인터뷰 신청서 생성 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = InterviewFormCreateRequest.class)
		)
	)
	@PostMapping("/majors/{majorId}/interviews")
	public ApiResponse<InterviewFormResponse> createInterviewForm(
		@Parameter(description = "인터뷰 대상 전공자 memberId", required = true)
		@PathVariable Long majorId,
		@Valid @RequestBody InterviewFormCreateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		InterviewFormResponse response = interviewFormService.createInterviewForm(requesterId, majorId, request);
		return ApiResponse.success(response);
	}

	/**
	 * body: { "status": "ACCEPTED", "majorMessage": "..." }
	 * body: { "status": "REJECTED", "majorMessage": "..." }
	 * body: { "status": "COMPLETED" }
	 **/
	@Operation(
		summary = "인터뷰 신청서 상태 변경",
		description = "인터뷰 신청서의 상태를 변경한다. 예: ACCEPTED/REJECTED는 majorMessage가 필요할 수 있으며, 현재 상태에 따라 변경이 제한될 수 있다. 성공 시 ApiResponse.success()로 data 없이 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateInterviewStatusSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패/메시지 누락/상태 전이 불가 등)",
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
			description = "접근 권한 없음(요청 처리 권한이 없음)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "인터뷰 신청 정보를 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409",
			description = "충돌(현재 상태와 충돌)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "500",
			description = "서버 오류(스냅샷 누락 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "상태 변경 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = InterviewStatusUpdateRequest.class)
		)
	)
	@PatchMapping("/interviews/{interviewId}/status")
	public ApiResponse<Void> updateInterviewFormStatus(
		@Parameter(description = "인터뷰 신청서 ID", required = true)
		@PathVariable Long interviewId,
		@Valid @RequestBody InterviewStatusUpdateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		interviewFormService.updateInterviewFormStatus(requesterId, interviewId, request);
		return ApiResponse.success();
	}

	/*
	  Swagger 문서 전용 스키마
	  - 실제 런타임 응답 타입을 변경하지 않기 위해, 문서용 wrapper 클래스를 둔다
	*/

	@Schema(
		name = "ApiResponseInterviewFormResponseListWithPageMeta",
		description = "성공 응답(ApiResponse) - data에 InterviewFormResponse 리스트, meta에 PageMeta 포함"
	)
	static class GetMyInterviewFormsSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = InterviewFormResponse.class))
		public List<InterviewFormResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseInterviewFormResponse_GetDetail",
		description = "성공 응답(ApiResponse) - data에 InterviewFormResponse 포함"
	)
	static class GetMyInterviewFormDetailSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = InterviewFormResponse.class)
		public InterviewFormResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseInterviewFormResponse_Create",
		description = "성공 응답(ApiResponse) - 인터뷰 신청서 생성 결과"
	)
	static class CreateInterviewFormSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = InterviewFormResponse.class)
		public InterviewFormResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseVoid_Success",
		description = "성공 응답(ApiResponse) - data, meta, error가 없는 형태"
	)
	static class UpdateInterviewStatusSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(nullable = true)
		public Object data;

		@Schema(nullable = true)
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
