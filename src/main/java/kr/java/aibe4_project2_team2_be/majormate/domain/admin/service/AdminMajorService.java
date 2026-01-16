package kr.java.aibe4_project2_team2_be.majormate.domain.admin.service;

import jakarta.persistence.EntityNotFoundException;
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

    // ... (검색/필터/조회 메소드 생략 - 기존과 동일) ...
    // getAllRequests, getPendingRequests 등은 그대로 두시면 됩니다.
    // 여기서는 핵심 로직인 acceptRequest를 중심으로 수정합니다.

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

    public Page<AdminMajorReqDto> getAllRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED, ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED, ApplicationStatus.REVOKED),
                searchType, keyword, pageable
        ).map(AdminMajorReqDto::new);
    }

    public Page<AdminMajorReqDto> getPendingRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(
                List.of(ApplicationStatus.PENDING, ApplicationStatus.RESUBMITTED),
                searchType, keyword, pageable
        ).map(AdminMajorReqDto::new);
    }

    public Page<AdminMajorReqDto> getAcceptedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.ACCEPTED), searchType, keyword, pageable).map(AdminMajorReqDto::new);
    }

    public Page<AdminMajorReqDto> getRejectedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REJECTED), searchType, keyword, pageable).map(AdminMajorReqDto::new);
    }

    public Page<AdminMajorReqDto> getRevokedRequests(String searchType, String keyword, Pageable pageable) {
        return searchOrFilter(Collections.singletonList(ApplicationStatus.REVOKED), searchType, keyword, pageable).map(AdminMajorReqDto::new);
    }

    public AdminMajorReqDetailDto getRequestDetail(Long requestId) {
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
        return new AdminMajorReqDetailDto(request);
    }

    @Transactional
    public void acceptRequest(Long requestId, Long adminId) {

        // 1. 요청 정보 및 관리자 조회
        MajorRoleRequest request = majorRoleRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));

        MemberProfile admin = memberProfileRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_404));

        // 2. 요청 상태 검증 (PENDING 또는 RESUBMITTED 상태만 승인 가능)
        if (request.getApplicationStatus() != ApplicationStatus.PENDING &&
                request.getApplicationStatus() != ApplicationStatus.RESUBMITTED) {
            throw new BusinessException(ErrorCode.COMMON_400);
        }

        String originalUrl = request.getDocumentUrl();
        String newUrl;

        // 3. 파일 이동 (S3 transferFile 사용)
        if (originalUrl != null && !originalUrl.isBlank()) {
            try {
                String extension = "";
                if (originalUrl.contains(".")) {
                    extension = originalUrl.substring(originalUrl.lastIndexOf("."));
                }
                // 파일 경로: major/{memberId}/{uuid}.확장자
                String newKey = "major/" + request.getMemberProfile().getMemberId() + "/" + UUID.randomUUID() + extension;

                // [수정] copyFile -> transferFile (다운로드 후 업로드)
                newUrl = s3FileService.transferFile(originalUrl, newKey);

                // 이동된 새 경로로 URL 업데이트
                request.updateDocumentUrl(newUrl);

            } catch (Exception e) {
                // 파일 이동 실패 시 로그만 남기고 승인 처리는 계속 진행 (마이그레이션 과도기 대응)
                // log.error("파일 이동 실패: {}", e.getMessage());
                System.err.println("파일 이동 실패 (승인은 진행됨): " + e.getMessage());
            }
        }

        // 4. 회원 권한 변경 (전공자 등업)
        // 주의: 여기서 MEMBER_400_INVALID_ROLE_TRANSITION 에러가 발생한다면,
        // DB의 해당 회원 status가 이미 전공자이거나 변경 불가능한 상태인지 확인해야 합니다.
        MemberProfile memberProfile = request.getMemberProfile();
        memberProfile.grantMajorRole();

        // 5. 요청 상태 승인으로 변경 (Entity에 approve 메서드가 있다고 가정, 없으면 setter 사용)
        request.accept(admin);

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