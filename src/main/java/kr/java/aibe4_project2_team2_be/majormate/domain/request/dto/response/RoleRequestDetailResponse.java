package kr.java.aibe4_project2_team2_be.majormate.domain.request.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import kr.java.aibe4_project2_team2_be.majormate.domain.request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleRequestDetailResponse {
	private Long id;
	private String memberName;
	private String content;
	private String documentUrl;
	private ApplicationStatus applicationStatus;
	private LocalDateTime createdAt;
	private LocalDateTime decidedAt;
	private List<RequestHistoryResponse> histories;

	public static RoleRequestDetailResponse from(MajorRoleRequest majorRoleRequest) {
		return RoleRequestDetailResponse.builder()
			.id(majorRoleRequest.getRequestId())
			.memberName(majorRoleRequest.getMember().getName())
			.content(majorRoleRequest.getContent())
			.documentUrl(majorRoleRequest.getDocumentUrl())
			.applicationStatus(majorRoleRequest.getApplicationStatus())
			.createdAt(majorRoleRequest.getCreatedAt())
			.decidedAt(majorRoleRequest.getDecidedAt())
			.histories(majorRoleRequest.getStatusHistories().stream()
				.map(RequestHistoryResponse::from)
				.collect(Collectors.toList()))
			.build();
	}
}
