package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.repository.AdminMajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMajorRoleRequestService {

    private final AdminMajorRoleRequestRepository majorRoleRequestRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final S3FileService s3Service;

    // 1. 관리자 - 요청 목록 조회 (대기중 & 재제출 상태만)
    public List<AdminMajorRoleRequest> getPendingRequests() {
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED)
        );
    }

    // 2. 관리자 - 요청 상세 조회
    public AdminMajorRoleRequest getRequestDetail(Long requestId) {
        return majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
    }

    // 3. 관리자 - 승인
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        AdminMajorRoleRequest request = getRequestDetail(requestId);

        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 요청 상태 변경 (accept)
        request.accept(admin);

        // 학생의 권한을 전공자(MAJOR)로 변경
        // [주의] Entity 필드명이 memberprofile이므로 getter도 getMemberprofile() 입니다.
        request.getMemberprofile().updateRole(MemberRole.MAJOR);
    }

    // 4. 관리자 - 반려
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        AdminMajorRoleRequest request = getRequestDetail(requestId);

        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));

        // 요청 상태 변경 (REJECTED) 및 사유 저장
        request.reject(admin, reason);
    }
}