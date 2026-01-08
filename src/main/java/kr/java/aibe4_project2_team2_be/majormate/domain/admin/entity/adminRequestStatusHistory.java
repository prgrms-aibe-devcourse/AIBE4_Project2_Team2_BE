package kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "major_role_request_status_history")
@Getter
@NoArgsConstructor
public class adminRequestStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private adminMajorRoleRequest request;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true, length = 20, name = "from_status")
	private ApplicationStatus fromStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, name = "to_status")
	private ApplicationStatus toStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "changed_by", nullable = false)
	private Member changedBy;

	@Column(nullable = false, length = 512, name = "message")
	private String message;

	@Column(length = 512, name = "reason")
	private String reason;

	@Column(name = "changed_at", nullable = false, updatable = false)
	private LocalDateTime changedAt;

	@PrePersist
	public void prePersist() {
		this.changedAt = LocalDateTime.now();
	}

	public static adminRequestStatusHistory createHistory(adminMajorRoleRequest request, ApplicationStatus from, ApplicationStatus to, Member actor, String reason) {
		adminRequestStatusHistory history = new adminRequestStatusHistory();
		history.request = request;
		history.fromStatus = from;
		history.toStatus = to;
		history.changedBy = actor;
		history.message = request.getComment();
		history.reason = reason;
		history.changedAt = LocalDateTime.now();
		return history;
	}
}
