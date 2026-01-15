package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

	Optional<MemberProfile> findByEmail(String email);

	Optional<MemberProfile> findByUsername(String username);

	@Query("select mp from MemberProfile mp left join fetch mp.academic where mp.memberId = :memberId")
	Optional<MemberProfile> findWithAcademicByMemberId(@Param("memberId") Long memberId);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
