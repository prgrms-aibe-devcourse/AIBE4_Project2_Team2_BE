package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.adminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class adminRoleRequestResponse {
	private Long id;
	private String university;
	private String major;
	private ApplicationStatus applicationStatus;
	private String comment;
	private String reason;
	private LocalDateTime createdAt;
	private LocalDateTime decidedAt;

	public static adminRoleRequestResponse from(adminMajorRoleRequest majorRoleRequest) {
		return adminRoleRequestResponse.builder()
			.id(majorRoleRequest.getRequestId())
			.university(majorRoleRequest.getUniversity())
			.major(majorRoleRequest.getMajor())
			.applicationStatus(majorRoleRequest.getApplicationStatus())
			.comment(majorRoleRequest.getComment())

			.createdAt(majorRoleRequest.getCreatedAt())
			.decidedAt(majorRoleRequest.getDecidedAt())
			.build();
	}
}
