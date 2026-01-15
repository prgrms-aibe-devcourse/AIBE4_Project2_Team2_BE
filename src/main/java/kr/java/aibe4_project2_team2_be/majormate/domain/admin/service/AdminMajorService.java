package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminMajorReqDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminMajorReqDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminMajorReqDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository.MajorRoleRequestRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMajorService {

    private final MajorRoleRequestRepository majorRoleRequestRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final S3FileService s3FileService;

    // [공통 검색/필터 로직]
    private Page<MajorRoleRequest> searchOrFilter(List<ApplicationStatus> statuses, String searchType, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            if ("id".equals(searchType)) {
                try {
                    Long rId = Long.parseLong(keyword);
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
    public Page<AdminMajorReqDto> getAllRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED, ApplicationStatus.REVOKED),
                searchType, keyword, pageable
        ).map(AdminMajorReqDto::new); // 여기서 DTO로 변환!
    }

    // 1-2. 대기 목록
    public Page<AdminMajorReqDto> getPendingRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED),
                searchType, keyword, pageable
        ).map(AdminMajorReqDto::new);
    }

    // 1-3. 승인된 목록
    public Page<AdminMajorReqDto> getAcceptedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.ACCEPTED), searchType, keyword, pageable)
                .map(AdminMajorReqDto::new);
    }

    // 1-4. 반려된 목록
    public Page<AdminMajorReqDto> getRejectedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REJECTED), searchType, keyword, pageable)
                .map(AdminMajorReqDto::new);
    }

    // 1-5. 자격 박탈 목록
    public Page<AdminMajorReqDto> getRevokedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REVOKED), searchType, keyword, pageable)
                .map(AdminMajorReqDto::new);
    }

    // 1-6. 상세 조회
    public AdminMajorReqDetailDto getRequestDetail(Long requestId) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));


        return new AdminMajorReqDetailDto(request);
    }

    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {

        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));

        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_404));

        if (request.getApplicationStatus() != ApplicationStatus.PENDING &&
                request.getApplicationStatus() != ApplicationStatus.RESUBMITTED) {
            throw new BusinessException(ErrorCode.COMMON_400);
        }

        String originalUrl = request.getDocumentUrl();
        if (originalUrl != null && !originalUrl.isBlank()) {
            String extension = "";
            if (originalUrl.contains(".")) {
                extension = originalUrl.substring(originalUrl.lastIndexOf("."));
            }
            String newKey = "major/" + request.getMemberProfile().getMemberId() + "/" + UUID.randomUUID() + extension;
            String newUrl = s3FileService.moveFile(originalUrl, newKey);
            request.updateDocumentUrl(newUrl);
        }

        request.accept(admin);
        request.getMemberProfile().grantMajorRole();
    }

    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.reject(admin, reason);
    }

    @Transactional
    public void revokeMemberMajorRole(Long requestId, Long adminId, String reason) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("관리자 정보를 찾을 수 없습니다."));
        request.revoke(admin, reason);
        request.getMemberProfile().revokeMajorRole();
    }

    @Transactional
    public void deleteExpiredDocuments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(5);
        List<MajorRoleRequest> expiredRequests = majorRoleRequestRepository.findByDecidedAtBeforeAndDocumentUrlIsNotNull(cutoffDate);

        int successCount = 0;
        for (MajorRoleRequest req : expiredRequests) {
            try {
                s3FileService.delete(req.getDocumentUrl());
                req.expireDocumentUrl();
                successCount++;
            } catch (Exception e) {
                System.err.println("증빙 서류 삭제 실패 (ID: " + req.getRequestId() + "): " + e.getMessage());
            }
        }
        if (successCount > 0) {
            System.out.println("총 " + successCount + "건의 만료된 증빙 서류를 삭제했습니다.");
        }
    }
}