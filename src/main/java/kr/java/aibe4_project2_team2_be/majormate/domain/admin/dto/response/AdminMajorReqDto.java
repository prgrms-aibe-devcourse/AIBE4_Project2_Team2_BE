package kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response;

import java.time.LocalDateTime;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import lombok.Getter;

@Getter
public class AdminMajorReqDto {
    private final Long id;
    private final String memberName;
    private final String universityName;
    private final String majorName;
    private final String applicationStatus;
    private final LocalDateTime createdAt;

    public AdminMajorReqDto(MajorRoleRequest entity) {
        this.id = entity.getRequestId();
        this.memberName = (entity.getMemberProfile() != null) ? entity.getMemberProfile().getNickname() : "(탈퇴)";
        this.universityName = entity.getUniversity();
        this.majorName = entity.getMajor();
        this.applicationStatus = entity.getApplicationStatus().getDescription();
        this.createdAt = entity.getCreatedAt();
    }
}