package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestHistoryResponse {
	private Long id;
	private String memberName;
	private ApplicationStatus oldStatus;
	private ApplicationStatus newStatus;
	private String changedBy;
	private String reason;
	private LocalDateTime changedAt;

	public static RequestHistoryResponse from(RequestStatusHistory history) {
		String changedByName = (history.getChangedBy() != null) ? history.getChangedBy().getName() : "null";

		return RequestHistoryResponse.builder()
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
