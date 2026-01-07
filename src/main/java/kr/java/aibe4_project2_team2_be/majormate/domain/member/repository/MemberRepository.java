package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByMemberId(Long memberId);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByUsername(String username);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
}
