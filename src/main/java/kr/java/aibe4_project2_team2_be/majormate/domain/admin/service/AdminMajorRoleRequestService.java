package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest; // 원본 Entity
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository; // 원본 Repo
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMajorRoleRequestService {

    // 관리자용 가짜 Repo가 아니라, 진짜 Repo를 주입받습니다.
    private final MajorRoleRequestRepository majorRoleRequestRepository;
    private final MemberProfileRepository memberProfileRepository;

    /**
     * 1. 관리자 - 대기 중인 요청 목록 조회
     * (PENDING, RESUBMITTED 상태만 가져옴)
     */
    public List<MajorRoleRequest> getPendingRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED)
        );
    }

    /**
     * 2. 관리자 - 요청 상세 조회
     */
    public MajorRoleRequest getRequestDetail(Long requestId) {
        return majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
    }

    /**
     * 3. 관리자 - 승인 처리
     */
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        // 1. 요청 조회 (Entity 가져오기)
        MajorRoleRequest request = getRequestDetail(requestId);

        // 2. 관리자 정보 조회
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 3. 승인 로직 수행 (Entity 내부 메서드 호출)
        request.accept(admin);

        // 4. 회원의 권한(Role)을 전공자(MAJOR)로 변경
        // 주의: getMemberProfile() 인지 getMember() 인지 Entity 파일 확인 필요 (현재 파일 기준 memberProfile)
        request.getMemberProfile().updateRole(MemberRole.MAJOR);
    }

    /**
     * 4. 관리자 - 반려 처리
     */
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        // 1. 요청 조회
        MajorRoleRequest request = getRequestDetail(requestId);

        // 2. 관리자 정보 조회
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 3. 반려 로직 수행 (Entity 내부 메서드 호출)
        request.reject(admin, reason);
    }
}