package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AdminRoleRequestDetailResponse {
    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String universityName;
    private String majorName;
    private String content;
    private String documentUrl;
    private ApplicationStatus applicationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private List<AdminRequestHistoryResponse> histories;

    public static AdminRoleRequestDetailResponse from(AdminMajorRoleRequest adminmajorRoleRequest) {
        return AdminRoleRequestDetailResponse.builder()
                .id(adminmajorRoleRequest.getRequestId())
                .name(adminmajorRoleRequest.getMember().getName())
                .nickname(adminmajorRoleRequest.getNickname())
                .username(adminmajorRoleRequest.getMember().getUsername())
                .universityName(adminmajorRoleRequest.getUniversity())
                .majorName(adminmajorRoleRequest.getMajor())
                .content(adminmajorRoleRequest.getComment())
                .documentUrl(adminmajorRoleRequest.getDocumentUrl())
                .applicationStatus(adminmajorRoleRequest.getApplicationStatus())
                .createdAt(adminmajorRoleRequest.getCreatedAt())
                .decidedAt(adminmajorRoleRequest.getDecidedAt())
                .histories(adminmajorRoleRequest.getStatusHistories().stream()
                        .map(AdminRequestHistoryResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
