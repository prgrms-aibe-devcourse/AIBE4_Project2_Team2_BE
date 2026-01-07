package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;

@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {
    List<MajorRoleRequest> findAllByMember_MemberIdOrderByCreatedAtDesc(Long memberId);
}
