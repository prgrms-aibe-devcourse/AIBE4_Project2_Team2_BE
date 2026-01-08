package kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import lombok.Getter;

@Getter
public class CustomOAuth2User implements OAuth2User {

	private final Long memberId;
	private final String role;
	private final Map<String, Object> attributes;

	public CustomOAuth2User(MemberProfile memberProfile, Map<String, Object> attributes) {
		// Extract member info immediately to avoid LazyInitializationException
		this.memberId = memberProfile.getMemberId();
		this.role = memberProfile.getRole().name();
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
			new SimpleGrantedAuthority(role)
		);
	}

	@Override
	public String getName() {
		return String.valueOf(memberId);
	}

	public Long getMemberId() {
		return memberId;
	}

	public String getRole() {
		return role;
	}
}
