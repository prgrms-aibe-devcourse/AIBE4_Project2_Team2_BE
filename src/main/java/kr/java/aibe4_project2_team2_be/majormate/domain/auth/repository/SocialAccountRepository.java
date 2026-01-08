package kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.AuthProvider;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

	Optional<SocialAccount> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

	List<SocialAccount> findByMemberProfile(MemberProfile memberProfile);

	boolean existsByMemberProfile(MemberProfile memberProfile);
}
