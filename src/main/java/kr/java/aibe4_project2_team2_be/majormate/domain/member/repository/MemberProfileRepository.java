package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

	Optional<MemberProfile> findByMemberId(Long memberId);

	Optional<MemberProfile> findByEmail(String email);

	Optional<MemberProfile> findByUsername(String username);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
