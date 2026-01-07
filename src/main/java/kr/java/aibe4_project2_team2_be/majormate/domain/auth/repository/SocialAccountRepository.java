package kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    List<SocialAccount> findByMember(Member member);

    boolean existsByMember(Member member);
}
