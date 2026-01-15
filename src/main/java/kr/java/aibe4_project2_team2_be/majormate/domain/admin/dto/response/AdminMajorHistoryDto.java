package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import lombok.Getter;

@Getter
public class AdminMajorHistoryDto {
    private final LocalDateTime changedAt;
    private final String toStatus;
    private final String changedByNickname;
    private final String reason;

    public AdminMajorHistoryDto(RequestStatusHistory history) {
        this.changedAt = history.getChangedAt();
        this.toStatus = history.getToStatus().name();
        this.changedByNickname = (history.getChangedBy() != null) ? history.getChangedBy().getNickname() : "(알수없음)";
        this.reason = history.getReason();
    }
}