package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request.RequestRejectRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request.RoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response.RoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.service.MajorRoleRequestService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/major-requests")
@RequiredArgsConstructor
@Tag(name = "Major Role Request", description = "전공자 인증 요청 API")
public class MajorRoleRequestController {

	private final MajorRoleRequestService majorRoleRequestService;

	// 1. 전공자 인증 요청 등록
	@Operation(summary = "전공자 인증 요청 등록", description = "전공자 인증을 위한 요청을 등록합니다.")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Long> createRequest(
		@Valid @RequestPart("request") RoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Long requestId = majorRoleRequestService.createRequest(memberId, requestDto, file);
		return ApiResponse.success(requestId);
	}

	// 2. 전공자 인증 요청 재제출
	@Operation(summary = "전공자 인증 요청 재제출", description = "반려된 요청을 수정하여 재제출합니다.")
	@PutMapping(value = "/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Void> resubmitRequest(
		@PathVariable Long requestId,
		@Valid @RequestPart("request") RoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorRoleRequestService.resubmitRequest(requestId, memberId, requestDto.getContent(), file);
		return ApiResponse.success(null);
	}

	@Operation(summary = "내 요청 목록 조회", description = "자신이 신청한 전공자 인증 요청 목록을 조회합니다.")
	@GetMapping("/me")
	public ApiResponse<List<RoleRequestResponse>> getMyRequests(
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		List<RoleRequestResponse> responses = majorRoleRequestService.getMyRequests(memberId);
		return ApiResponse.success(responses);
	}

	@Operation(summary = "전공자 인증 요청 상세 조회", description = "특정 전공자 인증 요청의 상세 정보를 조회합니다.")
	@GetMapping("/{requestId}")
	public ApiResponse<RoleRequestDetailResponse> MyGetRequestDetail(
		@PathVariable Long requestId
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		RoleRequestDetailResponse response = majorRoleRequestService.MyGetRequestDetail(requestId, memberId);
		return ApiResponse.success(response);
	}

}
