package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleRequestResponse {
	private Long id;
	private String university;
	private String major;
	private ApplicationStatus applicationStatus;
	private String comment;
	private String reason;
	private LocalDateTime createdAt;
	private LocalDateTime decidedAt;

	public static RoleRequestResponse from(MajorRoleRequest majorRoleRequest) {
		return RoleRequestResponse.builder()
			.id(majorRoleRequest.getRequestId())
			.university(majorRoleRequest.getUniversityName())
			.major(majorRoleRequest.getMajorName())
			.applicationStatus(majorRoleRequest.getApplicationStatus())
			.comment(majorRoleRequest.getContent())

			.createdAt(majorRoleRequest.getCreatedAt())
			.decidedAt(majorRoleRequest.getDecidedAt())
			.build();
	}
}
