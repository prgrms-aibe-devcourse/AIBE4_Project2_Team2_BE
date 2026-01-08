package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.adminRequestStatusHistory;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class adminRequestHistoryResponse {
    private Long id;
    private String memberName;
    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private String changedBy;
    private String reason;
    private LocalDateTime changedAt;

    public static adminRequestHistoryResponse from(adminRequestStatusHistory adminhistory) {
        String changedByName = (adminhistory.getChangedBy() != null) ? adminhistory.getChangedBy().getName() : "null";

        return adminRequestHistoryResponse.builder()
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
