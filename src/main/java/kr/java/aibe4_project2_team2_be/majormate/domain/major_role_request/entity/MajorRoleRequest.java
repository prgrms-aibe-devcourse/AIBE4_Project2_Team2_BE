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
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
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
    private MemberProfile memberProfile;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "university", nullable = false, length = 100)
    private String university;

    @Column(name = "major", nullable = false, length = 100)
    private String major;

    @Column(name = "comment", nullable = false, length = 512)
    private String comment;

    @Column(name = "document_url", nullable = true, length = 512)
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

    @Column(name = "reason", length = 512)
    private String reason;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestStatusHistory> statusHistories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 2. 파일 만료 처리 메서드 추가 (클래스 내부에 추가)
    public void expireDocumentUrl() {
        this.documentUrl = null;
    }

    public static MajorRoleRequest createRequest(MemberProfile memberProfile, String university, String major,
                                                 String comment, String documentUrl) {
        MajorRoleRequest request = new MajorRoleRequest();
        request.memberProfile = memberProfile;
        request.nickname = memberProfile.getNickname();
        request.university = university;
        request.major = major;
        request.comment = comment;
        request.documentUrl = documentUrl;
        request.applicationStatus = ApplicationStatus.PENDING; // 초기 상태는 대기

        // 초기 이력 생성
        request.statusHistories.add(
                RequestStatusHistory.createHistory(request, null, ApplicationStatus.PENDING, memberProfile, "")
        );

        return request;
    }

    // 승인
    public void accept(MemberProfile decider) {
        // 이 검증 메서드가 이제 '대기'와 '재신청'을 같이 넣어야한다.
        validatePendingStatus();
        ApplicationStatus oldStatus = this.applicationStatus;
        this.applicationStatus = ApplicationStatus.ACCEPTED;
        this.decider = decider;
        this.decidedAt = LocalDateTime.now();

        this.statusHistories.add(
                RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, "")
        );
    }

    // 반려
    public void reject(MemberProfile decider, String reason) {
        validatePendingStatus(); // 여기도 동일한 검증 로직 사용

        ApplicationStatus oldStatus = this.applicationStatus;
        this.applicationStatus = ApplicationStatus.REJECTED;
        this.decider = decider;
        this.reason = reason;
        this.decidedAt = LocalDateTime.now();

        this.statusHistories.add(
                RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, reason)
        );
    }

    // 재신청
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
                RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, this.memberProfile, "")
        );

    }

    // 자격 박탈됨 추가 ( 형민)
    public void revoke(MemberProfile decider, String reason) {
        // 승인된 상태에서만 박탈 가능
        if (this.applicationStatus != ApplicationStatus.ACCEPTED) {
            throw new IllegalStateException("승인된 상태의 요청만 자격을 박탈할 수 있습니다.");
        }

        ApplicationStatus oldStatus = this.applicationStatus;
        this.applicationStatus = ApplicationStatus.REVOKED; // 상태 변경
        this.decider = decider;
        this.decidedAt = LocalDateTime.now();
        this.reason = reason;

        // 이력 저장
        this.statusHistories.add(
                RequestStatusHistory.createHistory(this, oldStatus, this.applicationStatus, decider, reason)
        );
    }
    // 검증 로직
    private void validatePendingStatus() {
        // [디버깅용 로그] 현재 상태가 무엇인지 콘솔에 찍어봅니다.
        System.out.println(">>> 검증 중... 현재 상태: " + this.applicationStatus);

        if (this.applicationStatus != ApplicationStatus.PENDING
                && this.applicationStatus != ApplicationStatus.RESUBMITTED) {
            throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_INVALID_ROLE_TRANSITION);
        }
    }

}
