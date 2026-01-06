package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import lombok.Getter;

@Getter
public class MemberProfileResponse {
	private final String name;
	private final String nickname;
	private final String username;
	private final String profileImageUrl;
	private final String university;
	private final String major;

	public MemberProfileResponse(
		String name, String nickname, String username, String profileImageUrl, String university, String major
	) {
		this.name = name;
		this.nickname = nickname;
		this.username = username;
		this.profileImageUrl = profileImageUrl;
		this.university = university;
		this.major = major;
	}
}
