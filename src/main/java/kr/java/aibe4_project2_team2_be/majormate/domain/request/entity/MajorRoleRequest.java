package kr.java.aibe4_project2_team2_be.majormate.domain.request.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "major_role_request")
@Getter
@NoArgsConstructor
public class MajorRoleRequest extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false, length = 512)
	private String content;

	@Column(nullable = false, length = 512)
	private String documentUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, name = "application_status")
	private ApplicationStatus applicationStatus;

	@Column(name = "decided_at")
	private LocalDateTime decidedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "decided_by", nullable = false)
	private Member decider;

	public static MajorRoleRequest createRequest(Member member, String content, String documentUrl) {
		MajorRoleRequest request = new MajorRoleRequest();
		request.member = member;
		request.content = content;
		request.documentUrl = documentUrl;
		request.applicationStatus = ApplicationStatus.PENDING; // 초기 상태는 대기
		return request;
	}

	// 승인
	public void approve(Member decider) {
		validatePendingStatus(); // 대기 상태인지 검증
		this.applicationStatus = ApplicationStatus.ACCEPTED;
		this.decider = decider;
		this.decidedAt = LocalDateTime.now();
	}

	// 반려
	public void reject(Member decider) {
		validatePendingStatus();
		this.applicationStatus = ApplicationStatus.REJECTED;
		this.decider = decider;
		this.decidedAt = LocalDateTime.now();
	}

	// 재제출
	public void resubmit(String content, String documentUrl) {
		if (this.applicationStatus != ApplicationStatus.REJECTED) {
			throw new IllegalStateException("반려된 상태에서만 재제출이 가능합니다.");
		}
		this.content = content;
		this.documentUrl = documentUrl;
		this.applicationStatus = ApplicationStatus.RESUBMITTED; // 상태를 '재제출'로 변경
	}


	// 검증 로직
	private void validatePendingStatus() {
		if (this.applicationStatus != ApplicationStatus.PENDING && this.applicationStatus != ApplicationStatus.RESUBMITTED) {
			throw new IllegalStateException("심사가 가능한 상태(PENDING/RESUBMITTED)가 아닙니다.");
		}
	}


}
