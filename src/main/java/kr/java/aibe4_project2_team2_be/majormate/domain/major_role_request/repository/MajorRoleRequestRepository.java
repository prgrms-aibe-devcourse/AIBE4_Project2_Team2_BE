package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;

@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {
    List<MajorRoleRequest> findAllByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    // 특정 상태(예: PENDING, RESUBMITTED)의 요청 목록 조회 (최신순)
    List<MajorRoleRequest> findByApplicationStatusOrderByCreatedAtDesc(ApplicationStatus status);

    // 혹은 상태 리스트로 조회 (PENDING 이거나 RESUBMITTED 인 것들 한번에 조회)
    List<MajorRoleRequest> findByApplicationStatusInOrderByCreatedAtDesc(List<ApplicationStatus> statuses);
}
