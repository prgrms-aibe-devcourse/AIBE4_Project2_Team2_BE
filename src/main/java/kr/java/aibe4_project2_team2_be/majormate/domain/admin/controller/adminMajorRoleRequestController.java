package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.request.adminRequestRejectRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.request.adminRoleRequestCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.adminRoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.adminRoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.adminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.adminMajorRoleRequestService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Tag(name = "Major Role Request", description = "전공자 인증 요청 API")
public class adminMajorRoleRequestController {

	private final adminMajorRoleRequestService majorRoleRequestService;
	private final JwtTokenProvider jwtTokenProvider;

	// 1. 전공자 인증 요청 등록
	@Operation(summary = "전공자 인증 요청 등록", description = "전공자 인증을 위한 요청을 등록합니다.")
	@PostMapping(value = "major-requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Long> createRequest(
		// @RequestHeader("Authorization") String token,
		@Valid @RequestPart("request") adminRoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		// Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		Long memberId = 2L; // 테스트용 하드코딩
		Long requestId = majorRoleRequestService.createRequest(memberId, requestDto, file);
		return ApiResponse.success(requestId);
	}

	// 2. 전공자 인증 요청 재제출
	@Operation(summary = "전공자 인증 요청 재제출", description = "반려된 요청을 수정하여 재제출합니다.")
	@PutMapping(value = "major-requests/{requestId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<Void> resubmitRequest(
		@PathVariable Long requestId,
		// @RequestHeader("Authorization") String token,
		@Valid @RequestPart("request") adminRoleRequestCreateRequest requestDto,
		@RequestPart("file") MultipartFile file
	) {
		// Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		Long memberId = 2L; // 테스트용 하드코딩
		majorRoleRequestService.resubmitRequest(requestId, memberId, requestDto.getContent(), file);
		return ApiResponse.success(null);
	}

	@Operation(summary = "내 요청 목록 조회", description = "자신이 신청한 전공자 인증 요청 목록을 조회합니다.")
	@GetMapping("major-requests/me")
	public ApiResponse<List<adminRoleRequestResponse>> getMyRequests(
		// @RequestHeader("Authorization") String token
	) {
		// Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		Long memberId = 2L; // 테스트용 하드코딩
		List<adminRoleRequestResponse> responses = majorRoleRequestService.getMyRequests(memberId);
		return ApiResponse.success(responses);
	}

	@Operation(summary = "전공자 인증 요청 상세 조회", description = "특정 전공자 인증 요청의 상세 정보를 조회합니다.")
	@GetMapping("major-requests/{requestId}")
	public ApiResponse<adminRoleRequestDetailResponse> MyGetRequestDetail(
		@PathVariable Long requestId
		// @RequestHeader("Authorization") String token
	) {
		// Long memberId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		Long memberId = 2L; // 테스트용 하드코딩
		adminRoleRequestDetailResponse response = majorRoleRequestService.MyGetRequestDetail(requestId, memberId);
		return ApiResponse.success(response);
	}

	// ==========================================
	//  ⬇️ [추가됨] 관리자 기능 (목록 조회 & 상세 조회)
	// ==========================================

	// 3. 관리자 - 요청 목록 조회 (대기중 & 재제출)
	@Operation(summary = "관리자 - 요청 목록 조회", description = "대기 중(PENDING) 또는 재제출(RESUBMITTED) 상태의 요청 목록을 조회합니다.")
	@GetMapping(value = "admin/major-role-requests")
	public ApiResponse<List<adminRoleRequestResponse>> getPendingRequests(
		@RequestHeader("Authorization") String token
	) {
		// (필요 시 관리자 권한 체크 로직 추가 가능)

		// 서비스에서 엔티티 리스트 조회
		List<adminMajorRoleRequest> requests = majorRoleRequestService.getPendingRequests();

		// 엔티티 -> DTO 변환
		List<adminRoleRequestResponse> response = requests.stream()
			.map(adminRoleRequestResponse::from)
			.collect(Collectors.toList());

		return ApiResponse.success(response);
	}

	// 4. 관리자 - 요청 상세 조회 (이력 포함)
	@Operation(summary = "관리자 - 요청 상세 조회", description = "특정 전공자 인증 요청의 상세 정보와 히스토리를 조회합니다.")
	@GetMapping("admin/major-role-requests/{requestId}/detail")
	public ApiResponse<adminRoleRequestDetailResponse> getRequestDetail(
		@PathVariable Long requestId,
		@RequestHeader("Authorization") String token
	) {
		adminMajorRoleRequest request = majorRoleRequestService.getRequestDetail(requestId);
		adminRoleRequestDetailResponse response = adminRoleRequestDetailResponse.from(request);
		return ApiResponse.success(response);
	}

	// 5. 관리자 - 요청 승인
	@Operation(summary = "관리자 - 요청 승인", description = "전공자 인증 요청을 승인합니다.")
	@PostMapping("admin/major-role-requests/{requestId}/accept")
	public ApiResponse<Void> acceptRequest(
		@PathVariable Long requestId,
		@RequestHeader("Authorization") String token
	) {
		Long adminId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		majorRoleRequestService.acceptRequest(requestId, adminId);
		return ApiResponse.success(null);
	}

	// 6. 관리자 - 요청 반려
	@Operation(summary = "관리자 - 요청 반려", description = "전공자 인증 요청을 반려합니다.")
	@PostMapping("admin/major-role-requests/{requestId}/reject")
	public ApiResponse<Void> rejectRequest(
		@PathVariable Long requestId,
		@RequestHeader("Authorization") String token,
		@RequestBody adminRequestRejectRequest rejectDto
	) {
		Long adminId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
		majorRoleRequestService.rejectRequest(requestId, adminId, rejectDto.getReason());
		return ApiResponse.success(null);
	}
}