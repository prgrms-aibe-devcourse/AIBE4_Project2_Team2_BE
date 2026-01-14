package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import kr.java.aibe4_project2_team2_be.majormate.global.common.service.FileService;
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
    private final FileService fileService; // FileService 주입 필요 (S3FileService가 구현체)

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

    // [추가] 만료된 증빙 서류 일괄 삭제 로직
    @Transactional
    public void deleteExpiredDocuments() {
        // 기준 시간: 현재로부터 5일 전 (예: 오늘이 15일이면, 10일 이전에 처리된 건들을 찾음)
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(5);

        // 삭제 대상 조회
        List<MajorRoleRequest> expiredRequests = majorRoleRequestRepository.findByDecidedAtBeforeAndDocumentUrlIsNotNull(cutoffDate);

        int successCount = 0;
        for (MajorRoleRequest req : expiredRequests) {
            try {
                // 1. S3(supabase)에서 실제 파일 삭제
                fileService.delete(req.getDocumentUrl());

                // 2. DB에서 URL 정보 제거 (NULL 처리)
                req.expireDocumentUrl();

                successCount++;
            } catch (Exception e) {
                // 한 파일 삭제 실패가 전체 로직을 멈추지 않도록 로그만 남기고 계속 진행
                System.err.println("증빙 서류 삭제 실패 (ID: " + req.getRequestId() + "): " + e.getMessage());
            }
        }

        if (successCount > 0) {
            System.out.println("총 " + successCount + "건의 만료된 증빙 서류를 삭제했습니다.");
        }
    }
}