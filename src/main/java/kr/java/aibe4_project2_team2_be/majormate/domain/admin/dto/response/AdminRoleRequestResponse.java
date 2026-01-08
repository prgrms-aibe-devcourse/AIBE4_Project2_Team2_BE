package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminRoleRequestResponse {
    private Long id;
    private String university;
    private String major;
    private ApplicationStatus applicationStatus;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;

    public static AdminRoleRequestResponse from(AdminMajorRoleRequest adminmajorRoleRequest) {
        return AdminRoleRequestResponse.builder()
                .id(adminmajorRoleRequest.getRequestId())
                .university(adminmajorRoleRequest.getUniversity())
                .major(adminmajorRoleRequest.getMajor())
                .applicationStatus(adminmajorRoleRequest.getApplicationStatus())

                .createdAt(adminmajorRoleRequest.getCreatedAt())
                .decidedAt(adminmajorRoleRequest.getDecidedAt())
                .build();
    }
}
