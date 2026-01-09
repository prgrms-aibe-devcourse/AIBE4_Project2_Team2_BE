package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {
    List<MajorRoleRequest> findAllByMemberProfile_MemberIdOrderByCreatedAtDesc(Long memberId);

    // attributePaths = "member" -> "memberProfile"
    @EntityGraph(attributePaths = "memberProfile") // 형민
    List<MajorRoleRequest> findByApplicationStatusInOrderByCreatedAtDesc(List<ApplicationStatus> statuses);

}