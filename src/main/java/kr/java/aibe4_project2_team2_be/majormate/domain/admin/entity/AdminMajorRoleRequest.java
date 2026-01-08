package kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "major_role_request")
@Getter
@NoArgsConstructor
public class AdminMajorRoleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberProfile member;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "university", nullable = false, length = 100)
    private String university;

    @Column(name = "major", nullable = false, length = 100)
    private String major;

    @Column(name = "comment", nullable = false, length = 512)
    private String comment;

    @Column(name = "document_url", nullable = false, length = 512)
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
    private MemberProfile decider;

    @Column(name = "reason", length = 255)
    private String reason;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdminRequestStatusHistory> statusHistories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static AdminMajorRoleRequest createRequest(MemberProfile member, String university, String major, String comment, String documentUrl) {
        AdminMajorRoleRequest request = new AdminMajorRoleRequest();
        request.member = member;
        request.nickname = member.getNickname();
        request.university = university;
        request.major = major;
        request.comment = comment;
        request.documentUrl = documentUrl;
        request.applicationStatus = ApplicationStatus.PENDING; // 초기 상태는 대기

        // 초기 이력 생성
        request.statusHistories.add(
                AdminRequestStatusHistory.createHistory(request, null, ApplicationStatus.PENDING, member, "")
        );

        return request;
    }

    // 승인
    public void accept(MemberProfile decider) {
        validatePendingStatus(); // 대기 상태인지 검증
        ApplicationStatus oldStatus = this.applicationStatus;
        this.applicationStatus = ApplicationStatus.ACCEPTED;
        this.decider = decider;
        this.decidedAt = LocalDateTime.now();

        this.statusHistories.add(
                AdminRequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, "")
        );
    }

    // 반려
    public void reject(MemberProfile decider, String rejectMessage) {
        validatePendingStatus();

        if (rejectMessage == null || rejectMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("반려 시에는 반드시 반려 사유를 입력해야 합니다.");
        }
        ApplicationStatus oldStatus = this.applicationStatus;
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.decider = decider;
        this.decidedAt = LocalDateTime.now();
        this.reason = rejectMessage; // 반려 사유 저장

        this.statusHistories.add(
                AdminRequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, rejectMessage)
        );
    }

    // 재제출
    public void resubmit(String comment, String documentUrl) {

        if (this.applicationStatus != ApplicationStatus.REJECTED) {
            throw new IllegalStateException("반려된 상태에서만 재제출이 가능합니다.");
        }

        ApplicationStatus oldStatus = this.applicationStatus;
        this.comment = comment;
        this.documentUrl = documentUrl;
        this.applicationStatus = ApplicationStatus.RESUBMITTED; // 상태를 '재제출'로 변경
        this.decidedAt = null;
        this.decider = null;
        this.reason = null; // 재제출 시 반려 사유 초기화

        this.statusHistories.add(
                AdminRequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, this.member, "")
        );

    }

    // 검증 로직
    private void validatePendingStatus() {
        if (this.applicationStatus != ApplicationStatus.PENDING
                && this.applicationStatus != ApplicationStatus.RESUBMITTED) {
            throw new IllegalStateException("심사가 가능한 상태(PENDING/RESUBMITTED)가 아닙니다.");
        }
    }


}