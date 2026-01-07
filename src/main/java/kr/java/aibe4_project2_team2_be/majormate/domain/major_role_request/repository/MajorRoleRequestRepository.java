package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;


public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {

    // 혹은 상태 리스트로 조회 (PENDING 이거나 RESUBMITTED 인 것들 한번에 조회)
    @EntityGraph(attributePaths = "member")
    List<MajorRoleRequest> findByApplicationStatusInOrderByCreatedAtDesc(List<ApplicationStatus> statuses);
}
