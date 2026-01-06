package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;

public interface MemberAcademicRepository extends JpaRepository<MemberAcademic, Long> {
	MemberAcademic findByMember(Member member);
}
