package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {
    // 학생이 전공자 신청 요청 목록
    List<MajorRoleRequest> findAllByMemberProfile_MemberIdOrderByCreatedAtDesc(Long memberId);

    // 1. 상태별 조회 (페이징)
    @EntityGraph(attributePaths = "memberProfile")
    Page<MajorRoleRequest> findByApplicationStatusIn(List<ApplicationStatus> statuses, Pageable pageable);

    // 2. 닉네임 검색 (페이징)
    @EntityGraph(attributePaths = "memberProfile")
    Page<MajorRoleRequest> findByMemberProfile_NicknameContainingAndApplicationStatusIn(String nickname, List<ApplicationStatus> statuses, Pageable pageable);

    // 3. ID 검색 (페이징) - Entity 필드명 'requestId' 사용
    @EntityGraph(attributePaths = "memberProfile")
    Page<MajorRoleRequest> findByRequestIdAndApplicationStatusIn(Long requestId, List<ApplicationStatus> statuses, Pageable pageable);
}