package kr.java.aibe4_project2_team2_be.majormate.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberProfileRepository memberProfileRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberProfile memberProfile = memberProfileRepository.findById(Long.parseLong(username))
			.orElseThrow(() -> new NotFoundException(ErrorCodeNew.MEMBER_404));

		return new CustomUserDetails(
			memberProfile.getMemberId(),
			memberProfile.getEmail(),
			memberProfile.getPassword(),
			memberProfile.getRole().name()
		);
	}
}
