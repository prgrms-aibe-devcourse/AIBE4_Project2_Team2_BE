package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Hibernate;
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

    // [list 전체 검색 로직]
    private List<MajorRoleRequest> searchOrFilter(List<ApplicationStatus> statuses, String searchType, String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            if ("id".equals(searchType)) {
                try {
                    Long requestId = Long.parseLong(keyword);

                    return majorRoleRequestRepository.findByRequestIdAndApplicationStatusIn(requestId, statuses);
                } catch (NumberFormatException e) {
                    return Collections.emptyList(); // 숫자가 아니면 빈 목록 반환
                }
            } else if ("name".equals(searchType)) {
                return majorRoleRequestRepository.findByMemberProfile_NicknameContainingAndApplicationStatusInOrderByCreatedAtDesc(keyword, statuses);
            }
        }
        // 검색어가 없으면 전체 목록 (최신순)
        return majorRoleRequestRepository.findByApplicationStatusInOrderByCreatedAtDesc(statuses);
    }

    // 1-1. 전체 목록
    public List<MajorRoleRequest> getAllRequests(String searchType, String keyword) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED, ApplicationStatus.REVOKED),
                searchType, keyword
        );
    }

    // 1-2. 대기 목록
    public List<MajorRoleRequest> getPendingRequests(String searchType, String keyword) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED),
                searchType, keyword
        );
    }

    // 1-3. 승인된 목록
    public List<MajorRoleRequest> getAcceptedRequests(String searchType, String keyword) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.ACCEPTED), searchType, keyword);
    }

    // 1-4. 반려된 목록
    public List<MajorRoleRequest> getRejectedRequests(String searchType, String keyword) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REJECTED), searchType, keyword);
    }

    // 1-5. 자격 박탈 목록
    public List<MajorRoleRequest> getRevokedRequests(String searchType, String keyword) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REVOKED), searchType, keyword);
    }

    // 1-6. 상세 조회 (수정했던 부분 유지)
    public MajorRoleRequest getRequestDetail(Long requestId) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
        Hibernate.initialize(request.getStatusHistories());
        Hibernate.initialize(request.getMemberProfile());
        return request;
    }

    // 2-1. 승인
    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.accept(admin);
        request.getMemberProfile().grantMajorRole();
    }

    // 2-2. 반려
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.reject(admin, reason);
    }

    // 2-3. 자격 박탈
    @Transactional
    public void revokeMemberMajorRole(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = getRequestDetail(requestId);
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.revoke(admin, reason);
        request.getMemberProfile().revokeMajorRole(); // major => student 변경
    }
}