package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import lombok.Getter;

@Getter
public class AdminMajorReqDetailDto {
    private final Long id;
    private final String memberName;
    private final String universityName;
    private final String majorName;
    private final String applicationStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime decidedAt;
    private final String comment;
    private final String documentUrl;
    private final String reason;
    private final List<AdminMajorHistoryDto> histories;

    public AdminMajorReqDetailDto(MajorRoleRequest entity) {
        this.id = entity.getRequestId();
        this.memberName = (entity.getMemberProfile() != null) ? entity.getMemberProfile().getNickname() : "(탈퇴)";
        this.universityName = entity.getUniversity();
        this.majorName = entity.getMajor();
        this.applicationStatus = entity.getApplicationStatus().getDescription();
        this.createdAt = entity.getCreatedAt();
        this.decidedAt = entity.getDecidedAt();
        this.comment = entity.getComment();
        this.documentUrl = entity.getDocumentUrl();
        this.reason = entity.getReason();

        // 이력 정렬 및 변환
        this.histories = entity.getStatusHistories().stream()
                .sorted(Comparator.comparing(RequestStatusHistory::getChangedAt).reversed())
                .map(AdminMajorHistoryDto::new)
                .collect(Collectors.toList());
    }
}