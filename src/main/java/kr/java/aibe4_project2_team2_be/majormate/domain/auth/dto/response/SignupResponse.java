package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {

	private Long id;
	private String email;
	private String name;
	private String nickname;

	public static SignupResponse from(MemberProfile memberProfile) {
		return SignupResponse.builder()
			.id(memberProfile.getMemberId())
			.email(memberProfile.getEmail())
			.name(memberProfile.getName())
			.nickname(memberProfile.getNickname())
			.build();
	}
}
