package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class AdminMajorService {

    private final MajorRoleRequestRepository majorRoleRequestRepository;
    private final MemberProfileRepository memberProfileRepository;

    // [공통 검색/필터 로직]
    private Page<MajorRoleRequest> searchOrFilter(List<ApplicationStatus> statuses, String searchType, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            if ("id".equals(searchType)) {
                try {
                    Long rId = Long.parseLong(keyword);
                    // Repository 메서드명과 일치 (findByRequestId...)
                    return majorRoleRequestRepository.findByRequestIdAndApplicationStatusIn(rId, statuses, pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            } else if ("name".equals(searchType)) {
                return majorRoleRequestRepository.findByMemberProfile_NicknameContainingAndApplicationStatusIn(keyword, statuses, pageable);
            }
        }
        return majorRoleRequestRepository.findByApplicationStatusIn(statuses, pageable);
    }

    // 1-1. 전체 목록
    public Page<MajorRoleRequest> getAllRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED, ApplicationStatus.REVOKED),
                searchType, keyword, pageable
        );
    }

    // 1-2. 대기 목록
    public Page<MajorRoleRequest> getPendingRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED),
                searchType, keyword, pageable
        );
    }

    // 1-3. 승인된 목록
    public Page<MajorRoleRequest> getAcceptedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.ACCEPTED), searchType, keyword, pageable);
    }

    // 1-4. 반려된 목록
    public Page<MajorRoleRequest> getRejectedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REJECTED), searchType, keyword, pageable);
    }

    // 1-5. 자격 박탈 목록
    public Page<MajorRoleRequest> getRevokedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REVOKED), searchType, keyword, pageable);
    }

    // 1-6. 상세 조회 (Lazy Loading 해결 포함)
    public MajorRoleRequest getRequestDetail(Long requestId) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));

        Hibernate.initialize(request.getStatusHistories());
        Hibernate.initialize(request.getMemberProfile());

        return request;
    }

    // 2. 승인
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.accept(admin);
        request.getMemberProfile().grantMajorRole();
    }

    // 3. 반려
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.reject(admin, reason);
    }

    // 4. 자격 박탈
    @Transactional
    public void revokeMemberMajorRole(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.revoke(admin, reason);
        request.getMemberProfile().revokeMajorRole();
    }
}