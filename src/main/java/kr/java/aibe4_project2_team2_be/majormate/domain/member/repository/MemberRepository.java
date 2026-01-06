package kr.java.aibe4_project2_team2_be.majormate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByMemberId(Long memberId);

	Optional<Member> findByUsername(String username);

	Optional<Member> findByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);
}
