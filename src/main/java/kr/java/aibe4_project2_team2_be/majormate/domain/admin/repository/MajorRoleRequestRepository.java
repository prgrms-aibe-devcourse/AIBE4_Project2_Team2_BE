package kr.java.aibe4_project2_team2_be.majormate.domain.admin.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {
    List<MajorRoleRequest> findAllByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    // 혹은 상태 리스트로 조회 (PENDING 이거나 RESUBMITTED 인 것들 한번에 조회)
    @EntityGraph(attributePaths = "member")
    List<MajorRoleRequest> findByApplicationStatusInOrderByCreatedAtDesc(List<ApplicationStatus> statuses);
}
