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

    // 1. 신청자 닉네임 포함 검색 (LIKE %keyword%) // 형민
    @EntityGraph(attributePaths = "memberProfile")
    List<MajorRoleRequest> findByMemberProfile_NicknameContainingAndApplicationStatusInOrderByCreatedAtDesc(String nickname, List<ApplicationStatus> statuses);

    // 2. [수정] ID 검색 (RequestId -> Id 로 변경)
    // ID는 고유하므로 OrderBy가 필요 없고, 결과는 1개 또는 0개이지만
    // 공통 로직 처리를 위해 반환 타입을 List로 맞춥니다.
    @EntityGraph(attributePaths = "memberProfile")
    List<MajorRoleRequest> findByRequestIdAndApplicationStatusIn(Long id, List<ApplicationStatus> statuses);
}