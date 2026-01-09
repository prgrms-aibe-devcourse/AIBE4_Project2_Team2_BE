package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminRequestStatusHistory;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminRequestHistoryResponse {
    private Long id;
    private String memberName;
    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private String changedBy;
    private String reason;
    private LocalDateTime changedAt;

    public static AdminRequestHistoryResponse from(AdminRequestStatusHistory adminhistory) {
        String changedByName = (adminhistory.getChangedBy() != null) ? adminhistory.getChangedBy().getName() : "null";

        return AdminRequestHistoryResponse.builder()
                .id(adminhistory.getHistoryId())
                .memberName(adminhistory.getRequest().getMember().getName())
                .oldStatus(adminhistory.getFromStatus())
                .newStatus(adminhistory.getToStatus())
                .changedBy(changedByName)
                .reason(adminhistory.getMessage())
                .changedAt(adminhistory.getChangedAt())
                .build();
    }

}
