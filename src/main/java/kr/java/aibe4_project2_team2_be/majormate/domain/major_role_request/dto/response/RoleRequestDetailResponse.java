package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleRequestDetailResponse {
    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String universityName;
    private String majorName;
    private String content;
    private String documentUrl;
    private ApplicationStatus applicationStatus;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
    private List<RequestHistoryResponse> histories;

    public static RoleRequestDetailResponse from(MajorRoleRequest majorRoleRequest) {
        // 반려된 사유 가져오기
        String visibleReason = majorRoleRequest.getReason();
        // 만약 재신청 상태이면, 과거 이력 상태에서 반려 사유를 찾음. // 형민
        if (majorRoleRequest.getApplicationStatus() == ApplicationStatus.RESUBMITTED) {
            visibleReason = majorRoleRequest.getStatusHistories().stream()
                    .filter(h -> h.getToStatus() == ApplicationStatus.REJECTED)
                    .sorted(Comparator.comparing(RequestStatusHistory::getChangedAt).reversed())
                    .map(RequestStatusHistory::getReason)
                    .findFirst()
                    .orElse(null);
        }

        // 2. 응답 생성 (여기에 정렬 추가)
        return RoleRequestDetailResponse.builder()
                .id(majorRoleRequest.getRequestId())
                .name(majorRoleRequest.getMemberProfile().getName())
                .nickname(majorRoleRequest.getNickname())
                .username(majorRoleRequest.getMemberProfile().getUsername())
                .universityName(majorRoleRequest.getUniversity())
                .majorName(majorRoleRequest.getMajor())
                .content(majorRoleRequest.getComment())
                .documentUrl(majorRoleRequest.getDocumentUrl())
                .applicationStatus(majorRoleRequest.getApplicationStatus())
                .reason(visibleReason)
                .createdAt(majorRoleRequest.getCreatedAt())
                .decidedAt(majorRoleRequest.getDecidedAt())


                .histories(majorRoleRequest.getStatusHistories().stream()
                        // (1) 날짜 기준 내림차순(최신순) 정렬 추가 // reason 정렬임. // 형민
                        .sorted(Comparator.comparing(RequestStatusHistory::getChangedAt).reversed())
                        // (2) 그 다음 변환 수행
                        .map(RequestHistoryResponse::from)
                        .collect(Collectors.toList()))


                .build();
    }
}