// AdminnMajorRoleRequestController
package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.request.AdminRequestRejectRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminRoleRequestDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminRoleRequestResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminMajorRoleRequestService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Major Role Request", description = "관리자의 전공자 인증 요청 API")
public class AdminMajorRoleRequestController {

    private final AdminMajorRoleRequestService adminmajorRoleRequestService;
    private final JwtTokenProvider jwtTokenProvider;

    // 관리자 기능 (목록 조회 & 상세 조회)

    // 1. 관리자 - 요청 목록 조회 (대기중 & 재제출)
    @Operation(summary = "관리자 - 요청 목록 조회", description = "대기 중(PENDING) 또는 재제출(RESUBMITTED) 상태의 요청 목록을 조회합니다.")
    @GetMapping(value = "/major-role-requests/list")
    public ApiResponse<List<AdminRoleRequestResponse>> getPendingRequests(
            @RequestHeader("Authorization") String token
    ) {
        // 서비스에서 엔티티 리스트 조회
        List<AdminMajorRoleRequest> requests = adminmajorRoleRequestService.getPendingRequests();

        // 엔티티 -> DTO 변환
        List<AdminRoleRequestResponse> response = requests.stream()
                .map(AdminRoleRequestResponse::from)
                .collect(Collectors.toList());

        return ApiResponse.success(response);
    }

    // 2. 관리자 - 요청 상세 조회 (이력 포함)
    @Operation(summary = "관리자 - 요청 상세 조회", description = "특정 전공자 인증 요청의 상세 정보와 히스토리를 조회합니다.")
    @GetMapping("/major-role-requests/{requestId}/detail")
    public ApiResponse<AdminRoleRequestDetailResponse> getRequestDetail(
            @PathVariable Long requestId
            //@RequestHeader("Authorization") String token
    ) {
        AdminMajorRoleRequest request = adminmajorRoleRequestService.getRequestDetail(requestId);
        AdminRoleRequestDetailResponse response = AdminRoleRequestDetailResponse.from(request);
        return ApiResponse.success(response);
    }

    // 3. 관리자 - 요청 승인
    @Operation(summary = "관리자 - 요청 승인", description = "전공자 인증 요청을 승인합니다.")
    @PostMapping("/major-role-requests/{requestId}/accept")
    public ApiResponse<Void> acceptRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token
    ) {
        Long adminId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
        adminmajorRoleRequestService.acceptRequest(requestId, adminId);
        return ApiResponse.success(null);
    }

    // 4. 관리자 - 요청 반려
    @Operation(summary = "관리자 - 요청 반려", description = "전공자 인증 요청을 반려합니다.")
    @PostMapping("/major-role-requests/{requestId}/reject")
    public ApiResponse<Void> rejectRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String token,
            @RequestBody AdminRequestRejectRequest rejectDto
    ) {
        Long adminId = jwtTokenProvider.getMemberIdFromToken(token.substring(7));
        adminmajorRoleRequestService.rejectRequest(requestId, adminId, rejectDto.getReason());
        return ApiResponse.success(null);
    }
}