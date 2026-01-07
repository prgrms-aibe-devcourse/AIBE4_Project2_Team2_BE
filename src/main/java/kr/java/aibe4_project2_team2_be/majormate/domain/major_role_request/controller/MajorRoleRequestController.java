package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request.RoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.service.MajorRoleRequestService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/major-requests")
@RequiredArgsConstructor
@Tag(name = "Major Role Request", description = "전공자 인증 요청 API")
public class MajorRoleRequestController {

	private final MajorRoleRequestService majorRoleRequestService;
	private final JwtTokenProvider jwtTokenProvider;

	@Operation(summary = "전공자 인증 요청 등록", description = "전공자 인증을 위한 요청을 등록합니다.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Long> createRequest(
		@RequestHeader("Authorization") String token,
		@Valid @RequestPart("request") RoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		// Long memberId = 2L; // 테스트용 하드코딩
		Long requestId = majorRoleRequestService.createRequest(memberId, requestDto.getContent(), file);
		return ApiResponse.success(requestId);
	}

	@Operation(summary = "전공자 인증 요청 재제출", description = "반려된 요청을 수정하여 재제출합니다.")
	@PutMapping(value = "/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Void> resubmitRequest(
		@PathVariable Long requestId,
		@RequestHeader("Authorization") String token,
		@Valid @RequestPart("request") RoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		// Long memberId = 2L; // 테스트용 하드코딩
		majorRoleRequestService.resubmitRequest(requestId, memberId, requestDto.getContent(), file);
		return ApiResponse.success(null);
	}
}
