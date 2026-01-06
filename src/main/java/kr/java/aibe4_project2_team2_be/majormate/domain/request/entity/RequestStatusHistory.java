package kr.java.aibe4_project2_team2_be.majormate.domain.request.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "major_role_request_status_history")
@Getter
@NoArgsConstructor
public class RequestStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id", nullable = false)
	private MajorRoleRequest request;

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

	@Column(name = "changed_at", nullable = false, updatable = false)
	private LocalDateTime changedAt;

	@PrePersist
	public void prePersist() {
		this.changedAt = LocalDateTime.now();
	}

	public static RequestStatusHistory createHistory(MajorRoleRequest request, ApplicationStatus from, ApplicationStatus to, Member actor, String msg) {
		RequestStatusHistory history = new RequestStatusHistory();
		history.request = request;
		history.fromStatus = from;
		history.toStatus = to;
		history.changedBy = actor;
		history.message = msg;
		history.changedAt = LocalDateTime.now();
		return history;
	}
}
