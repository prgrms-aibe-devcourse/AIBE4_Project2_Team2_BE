package kr.java.aibe4_project2_team2_be.majormate.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberProfileRepository memberProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameInput) throws UsernameNotFoundException {
        MemberProfile memberProfile = null;

        // 1. 숫자 ID로 로그인 시도 (JWT/앱용)
        try {
            Long memberId = Long.parseLong(usernameInput);
            memberProfile = memberProfileRepository.findById(memberId).orElse(null);
        } catch (NumberFormatException e) {
            // "admin" 같은 문자열이면 에러가 나므로 무시하고 아래로 넘어감
        }

        // 2. 문자열 아이디로 로그인 시도 (관리자용)
        if (memberProfile == null) {
            memberProfile = memberProfileRepository.findByUsername(usernameInput)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + usernameInput));
        }

        return new CustomUserDetails(
                memberProfile.getMemberId(),
                memberProfile.getEmail(),
                memberProfile.getPassword(),
                memberProfile.getRole().name()
        );
    }
}