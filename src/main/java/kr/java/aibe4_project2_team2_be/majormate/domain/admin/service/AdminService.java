package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MajorRoleRequestRepository majorRoleRequestRepository;
    private final MemberProfileRepository memberProfileRepository;

    // ==========================
    // 1. 목록 조회 메서드 (Read)
    // ==========================

    // 1-1. 대기 중인 요청 목록 (PENDING, RESUBMITTED)
    public List<MajorRoleRequest> getPendingRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED)
        );
    }

    // 1-2. 승인된 요청 목록 (ACCEPTED) - [추가됨]
    public List<MajorRoleRequest> getAcceptedRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                Collections.singletonList(ApplicationStatus.ACCEPTED)
        );
    }

    // 1-3. 반려된 요청 목록 (REJECTED)
    public List<MajorRoleRequest> getRejectedRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                Collections.singletonList(ApplicationStatus.REJECTED)
        );
    }

    // 1-4. 자격 박탈된 요청 목록 (REVOKED)
    public List<MajorRoleRequest> getRevokedRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                Collections.singletonList(ApplicationStatus.REVOKED)
        );
    }

    // 1-5. 요청 상세 조회
    public MajorRoleRequest getRequestDetail(Long requestId) {
        return majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
    }

    // ==========================
    // 2. 상태 변경 메서드 (Write)
    // ==========================

    // 2-1. 승인 처리
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        request.accept(admin);
        request.getMemberProfile().grantMajorRole();
    }

    // 2-2. 반려 처리
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        request.reject(admin, reason);
    }

    // 2-3. 자격 박탈 처리
    @Transactional
    public void revokeMemberMajorRole(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 1. 요청 상태를 REVOKED로 변경 (Entity 메서드 호출)
        request.revoke(admin, reason);

        // 2. 실제 회원의 권한을 STUDENT로 강등
        request.getMemberProfile().revokeMajorRole();
    }
}