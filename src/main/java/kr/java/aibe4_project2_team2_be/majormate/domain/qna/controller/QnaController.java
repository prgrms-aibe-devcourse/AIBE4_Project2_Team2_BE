package kr.java.aibe4_project2_team2_be.majormate.domain.qna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.request.QnaRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.IdResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.PublicQnaResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QnaResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.service.QnaService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ErrorResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageMeta;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.PageResponses;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "QnA", description = "질문/답변 목록 조회 및 작성/수정/삭제 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QnaController {

	private final QnaService qnaService;

	@Operation(
		summary = "내 질문 목록 조회",
		description = "학생 기준으로 내가 작성한 질문 목록을 조회한다. 답변이 있으면 함께 포함될 수 있다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyQuestionsSuccessDoc.class)
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
	@GetMapping("/members/me/questions")
	public ApiResponse<List<QnaResponse>> getMyQuestions(
		@Parameter(description = "정렬 기준(기본값: CREATED_AT_DESC)", required = false)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<QnaResponse> page = qnaService.getMyQuestions(memberId, sort, pageable);
		return PageResponses.of(page);
	}

	@Operation(
		summary = "내 답변 목록 조회",
		description = "전공자 기준으로 내가 작성한 답변 목록을 조회한다. 질문 정보와 함께 포함될 수 있다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyAnswersSuccessDoc.class)
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
	@GetMapping("/members/me/answers")
	public ApiResponse<List<QnaResponse>> getMyAnswers(
		@Parameter(description = "정렬 기준(기본값: CREATED_AT_DESC)", required = false)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<QnaResponse> page = qnaService.getMyAnswers(memberId, sort, pageable);
		return PageResponses.of(page);
	}

	@Operation(
		summary = "전공자 공개 QnA 목록 조회",
		description = "공개 프로필에서 특정 전공자(majorId)에게 작성된 질문/답변 목록을 조회한다. 응답은 PageResponses.of(Page) 형태로 data와 meta(PageMeta)를 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMajorPublicQnaSuccessDoc.class)
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
	@GetMapping("/majors/{majorId}/qna")
	public ApiResponse<List<PublicQnaResponse>> getMajorPublicQnA(
		@Parameter(description = "전공자 memberId", required = true)
		@PathVariable Long majorId,
		@Parameter(description = "정렬 기준(기본값: CREATED_AT_DESC)", required = false)
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		@Parameter(description = "페이지네이션 정보(page, size, sort). sort 파라미터는 본 API의 sort(PageSort)로 별도 처리")
		Pageable pageable
	) {
		Page<PublicQnaResponse> page = qnaService.getMajorPublicQnA(majorId, sort, pageable);
		return PageResponses.of(page);
	}

	@Operation(
		summary = "질문 작성",
		description = "학생이 특정 전공자(majorId)에게 질문을 작성한다. 자기 자신에게 질문하거나 대상이 전공자가 아닌 경우 등은 에러로 처리될 수 있다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = CreateQuestionSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패, 대상 오류 등)",
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
			description = "권한 없음",
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
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "질문 작성 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = QnaRequest.class)
		)
	)
	@PostMapping("/majors/{majorId}/questions")
	public ApiResponse<IdResponse> createQuestion(
		@Parameter(description = "질문 대상 전공자 memberId", required = true)
		@PathVariable Long majorId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		IdResponse response = qnaService.createQuestion(memberId, majorId, request);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "답변 작성",
		description = "전공자가 특정 질문(questionId)에 대해 답변을 작성한다. 이미 답변이 존재하면 충돌로 처리될 수 있다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = CreateAnswerSuccessDoc.class)
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
			description = "권한 없음(MAJOR 권한 필요 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "질문을 찾을 수 없음",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409",
			description = "충돌(이미 답변이 존재)",
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
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "답변 작성 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = QnaRequest.class)
		)
	)
	@PostMapping("/questions/{questionId}/answer")
	public ApiResponse<IdResponse> createAnswer(
		@Parameter(description = "질문 ID", required = true)
		@PathVariable Long questionId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		IdResponse response = qnaService.createAnswer(memberId, questionId, request);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "내 답변 수정",
		description = "전공자가 본인이 작성한 답변(answerId)을 수정한다. 성공 시 ApiResponse.success()로 data 없이 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponseVoidSuccessDoc.class)
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
			description = "권한 없음(작성자가 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "답변을 찾을 수 없음",
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
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "답변 수정 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = QnaRequest.class)
		)
	)
	@PatchMapping("/answers/{answerId}")
	public ApiResponse<Void> updateMyAnswer(
		@Parameter(description = "답변 ID", required = true)
		@PathVariable Long answerId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.updateMyAnswer(memberId, answerId, request);
		return ApiResponse.success();
	}

	@Operation(
		summary = "내 질문 수정",
		description = "학생이 본인이 작성한 질문(questionId)을 수정한다. 답변이 달린 질문은 수정이 제한될 수 있다. 성공 시 ApiResponse.success()로 data 없이 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponseVoidSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패, 답변 달린 질문 수정 불가 등)",
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
			description = "권한 없음(작성자가 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "질문을 찾을 수 없음",
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
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		required = true,
		description = "질문 수정 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = QnaRequest.class)
		)
	)
	@PatchMapping("/questions/{questionId}")
	public ApiResponse<Void> updateMyQuestion(
		@Parameter(description = "질문 ID", required = true)
		@PathVariable Long questionId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.updateMyQuestion(memberId, questionId, request);
		return ApiResponse.success();
	}

	@Operation(
		summary = "내 질문 삭제",
		description = "학생이 본인이 작성한 질문(questionId)을 삭제한다. 답변이 달린 질문은 삭제가 제한될 수 있다. 성공 시 ApiResponse.success()로 data 없이 반환한다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiResponseVoidSuccessDoc.class)
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
			description = "권한 없음(작성자가 아님 등)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404",
			description = "질문을 찾을 수 없음",
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
	@DeleteMapping("/questions/{questionId}")
	public ApiResponse<Void> deleteMyQuestion(
		@Parameter(description = "질문 ID", required = true)
		@PathVariable Long questionId
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.deleteMyQuestion(memberId, questionId);
		return ApiResponse.success();
	}

	/*
	  Swagger 문서 전용 스키마
	  - 실제 런타임 응답 타입을 변경하지 않기 위해, 문서용 wrapper 클래스를 둔다
	*/

	@Schema(
		name = "ApiResponseQnaResponseListWithPageMeta",
		description = "성공 응답(ApiResponse) - data에 QnaResponse 리스트, meta에 PageMeta 포함"
	)
	static class GetMyQuestionsSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = QnaResponse.class))
		public List<QnaResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseQnaResponseListWithPageMeta_Answers",
		description = "성공 응답(ApiResponse) - data에 QnaResponse 리스트(답변 중심), meta에 PageMeta 포함"
	)
	static class GetMyAnswersSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = QnaResponse.class))
		public List<QnaResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponsePublicQnaResponseListWithPageMeta",
		description = "성공 응답(ApiResponse) - data에 PublicQnaResponse 리스트, meta에 PageMeta 포함"
	)
	static class GetMajorPublicQnaSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@ArraySchema(schema = @Schema(implementation = PublicQnaResponse.class))
		public List<PublicQnaResponse> data;

		@Schema(implementation = PageMeta.class)
		public PageMeta meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseIdResponse_CreateQuestion",
		description = "성공 응답(ApiResponse) - 질문/답변 생성 결과로 IdResponse 반환"
	)
	static class CreateQuestionSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = IdResponse.class)
		public IdResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseIdResponse_CreateAnswer",
		description = "성공 응답(ApiResponse) - 질문에 대한 답변 생성 결과로 IdResponse 반환"
	)
	static class CreateAnswerSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = IdResponse.class)
		public IdResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(
		name = "ApiResponseVoid_Success",
		description = "성공 응답(ApiResponse) - data, meta, error가 없는 형태"
	)
	static class ApiResponseVoidSuccessDoc {
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
