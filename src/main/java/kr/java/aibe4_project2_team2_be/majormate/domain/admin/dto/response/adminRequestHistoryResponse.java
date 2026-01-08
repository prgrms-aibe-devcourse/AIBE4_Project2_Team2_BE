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

	public static adminRequestHistoryResponse from(adminRequestStatusHistory history) {
		String changedByName = (history.getChangedBy() != null) ? history.getChangedBy().getName() : "null";

		return adminRequestHistoryResponse.builder()
			.id(history.getHistoryId())
			.memberName(history.getRequest().getMember().getName())
			.oldStatus(history.getFromStatus())
			.newStatus(history.getToStatus())
			.changedBy(changedByName)
			.reason(history.getMessage())
			.changedAt(history.getChangedAt())
			.build();
	}

}
