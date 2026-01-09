package kr.java.aibe4_project2_team2_be.majormate.domain.admin.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.entity.AdminMajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMajorRoleRequestRepository extends JpaRepository<AdminMajorRoleRequest, Long> {

    // [중요 수정] MemberProfile -> Memberprofile (대문자 P를 소문자 p로 변경)
    // Entity의 필드명이 'memberprofile'이므로 메서드 이름도 이를 따라야 합니다.
    List<AdminMajorRoleRequest> findAllByMemberprofile_MemberIdOrderByCreatedAtDesc(Long memberId);

    // [중요 수정] attributePaths도 "memberprofile" (소문자 p)로 일치시킴
    @EntityGraph(attributePaths = "memberprofile")
    List<AdminMajorRoleRequest> findByApplicationStatusInOrderByCreatedAtDesc(List<ApplicationStatus> statuses);
}
