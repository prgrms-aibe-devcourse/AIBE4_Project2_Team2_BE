package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "major_role_request")
@Getter
@NoArgsConstructor
public class MajorRoleRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId;

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

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "decided_at")
	private LocalDateTime decidedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "decided_by")
	private Member decider;

	@OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RequestStatusHistory> statusHistories = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	public static MajorRoleRequest createRequest(Member member, String content, String documentUrl) {
		MajorRoleRequest request = new MajorRoleRequest();
		request.member = member;
		request.content = content;
		request.documentUrl = documentUrl;
		request.applicationStatus = ApplicationStatus.PENDING; // 초기 상태는 대기
		
		// 초기 이력 생성
		request.statusHistories.add(
			RequestStatusHistory.createHistory(request, null, ApplicationStatus.PENDING, member, "전공자 인증 요청을 등록했습니다.")
		);
		
		return request;
	}

	// 승인
	public void accept(Member decider) {
		validatePendingStatus(); // 대기 상태인지 검증
		ApplicationStatus oldStatus = this.applicationStatus;
		this.applicationStatus = ApplicationStatus.ACCEPTED;
		this.decider = decider;
		this.decidedAt = LocalDateTime.now();

		this.statusHistories.add(
			RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, "전공자 지원 승인 되었습니다")
		);
	}

	// 반려
	public void reject(Member decider, String rejectMessage) {
		validatePendingStatus();

		if (rejectMessage == null || rejectMessage.trim().isEmpty()) {
			throw new IllegalArgumentException("반려 시에는 반드시 반려 사유를 입력해야 합니다.");
		}
		ApplicationStatus oldStatus = this.applicationStatus;
		this.applicationStatus = ApplicationStatus.REJECTED;
		this.decider = decider;
		this.decidedAt = LocalDateTime.now();

		this.statusHistories.add(
			RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, rejectMessage)
		);
	}

	// 재제출
	public void resubmit(String content, String documentUrl) {

		if (this.applicationStatus != ApplicationStatus.REJECTED) {
			throw new IllegalStateException("반려된 상태에서만 재제출이 가능합니다.");
		}

		ApplicationStatus oldStatus = this.applicationStatus;
		this.content = content;
		this.documentUrl = documentUrl;
		this.applicationStatus = ApplicationStatus.RESUBMITTED; // 상태를 '재제출'로 변경
		this.decidedAt = null;
		this.decider = null;

		this.statusHistories.add(
			RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, this.member, "반려 사유 확인 후 내용을 수정하여 재제출했습니다.")
		);

	}

	// 검증 로직
	private void validatePendingStatus() {
		if (this.applicationStatus != ApplicationStatus.PENDING && this.applicationStatus != ApplicationStatus.RESUBMITTED) {
			throw new IllegalStateException("심사가 가능한 상태(PENDING/RESUBMITTED)가 아닙니다.");
		}
	}


}
