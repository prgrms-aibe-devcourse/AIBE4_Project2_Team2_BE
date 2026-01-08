package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;

public interface MemberAcademicRepository extends JpaRepository<MemberAcademic, Long> {
	Optional<MemberAcademic> findByMemberProfile_MemberId(Long memberId);

	Optional<MemberAcademic> findByMemberProfile(MemberProfile memberProfile);

	List<MemberAcademic> findAllByMemberProfile_MemberIdIn(List<Long> memberIds);
}
