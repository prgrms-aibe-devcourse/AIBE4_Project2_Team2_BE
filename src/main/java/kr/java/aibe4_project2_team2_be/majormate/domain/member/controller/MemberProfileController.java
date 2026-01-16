package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberProfileUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberProfileService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ErrorResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원-내 정보", description = "내 프로필 조회/수정 및 프로필 이미지 업로드/삭제 API")
@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberProfileController {

	private final MemberProfileService memberProfileService;

	@Operation(
		summary = "내 프로필 조회",
		description = "현재 로그인한 사용자의 프로필 정보를 조회한다. 응답은 ApiResponse.success(data) 형태로 반환된다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = GetMyProfileSuccessDoc.class)
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
			responseCode = "404",
			description = "회원 정보를 찾을 수 없음",
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
	@GetMapping
	public ApiResponse<MemberProfileResponse> getMyProfile() {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.getMemberProfile(requesterId);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "내 프로필 수정",
		description = "현재 로그인한 사용자의 프로필 정보를 수정한다. 응답은 ApiResponse.success(data) 형태로 반환된다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateMyProfileSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(검증 실패/정책 위반)",
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
			description = "중복 값 충돌",
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
		description = "프로필 수정 요청 바디",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = MemberProfileUpdateRequest.class)
		)
	)
	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiResponse<MemberProfileResponse> updateMyProfile(
		@Valid @RequestBody MemberProfileUpdateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.updateMemberProfile(requesterId, request);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "내 프로필 이미지 업로드/변경",
		description = "멀티파트 파일로 프로필 이미지를 업로드하거나 교체한다. 응답은 ApiResponse.success(data) 형태로 반환된다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateProfileImageSuccessDoc.class)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400",
			description = "요청 값 오류(파일 누락/형식 오류 등)",
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
			description = "파일 처리/업로드 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<MemberProfileResponse> updateMyProfileImage(
		@RequestPart("file") MultipartFile imgFile
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.updateMemberProfileImage(requesterId, imgFile);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "내 프로필 이미지 삭제",
		description = "현재 로그인한 사용자의 프로필 이미지를 삭제한다. 응답은 ApiResponse.success(data) 형태로 반환된다."
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = DeleteProfileImageSuccessDoc.class)
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
			description = "파일 삭제 실패",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ErrorDoc.class)
			)
		)
	})
	@DeleteMapping("/profile-image")
	public ApiResponse<MemberProfileResponse> deleteMyProfileImage() {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.deleteMemberProfileImage(requesterId);
		return ApiResponse.success(response);
	}

	@Schema(name = "ApiResponseMemberProfileResponse_Get", description = "성공 응답(ApiResponse) - data에 MemberProfileResponse 포함")
	static class GetMyProfileSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = MemberProfileResponse.class)
		public MemberProfileResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(name = "ApiResponseMemberProfileResponse_Patch", description = "성공 응답(ApiResponse) - 내 프로필 수정 결과")
	static class UpdateMyProfileSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = MemberProfileResponse.class)
		public MemberProfileResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(name = "ApiResponseMemberProfileResponse_PutImage", description = "성공 응답(ApiResponse) - 프로필 이미지 업로드/변경 결과")
	static class UpdateProfileImageSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = MemberProfileResponse.class)
		public MemberProfileResponse data;

		@Schema(nullable = true, description = "페이지/추가 메타가 없는 API이므로 일반적으로 null")
		public Object meta;

		@Schema(nullable = true)
		public ErrorResponse error;
	}

	@Schema(name = "ApiResponseMemberProfileResponse_DeleteImage", description = "성공 응답(ApiResponse) - 프로필 이미지 삭제 결과")
	static class DeleteProfileImageSuccessDoc {
		@Schema(example = "true")
		public boolean success;

		@Schema(implementation = MemberProfileResponse.class)
		public MemberProfileResponse data;

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
