package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.adminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class adminRoleRequestDetailResponse {
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
	private List<adminRequestHistoryResponse> histories;

	public static adminRoleRequestDetailResponse from(adminMajorRoleRequest majorRoleRequest) {
		return adminRoleRequestDetailResponse.builder()
			.id(majorRoleRequest.getRequestId())
			.name(majorRoleRequest.getMember().getName())
			.nickname(majorRoleRequest.getNickname())
			.username(majorRoleRequest.getMember().getUsername())
			.universityName(majorRoleRequest.getUniversity())
			.majorName(majorRoleRequest.getMajor())
			.content(majorRoleRequest.getComment())
			.documentUrl(majorRoleRequest.getDocumentUrl())
			.applicationStatus(majorRoleRequest.getApplicationStatus())
			.createdAt(majorRoleRequest.getCreatedAt())
			.decidedAt(majorRoleRequest.getDecidedAt())
			.histories(majorRoleRequest.getStatusHistories().stream()
				.map(adminRequestHistoryResponse::from)
				.collect(Collectors.toList()))
			.build();
	}
}
