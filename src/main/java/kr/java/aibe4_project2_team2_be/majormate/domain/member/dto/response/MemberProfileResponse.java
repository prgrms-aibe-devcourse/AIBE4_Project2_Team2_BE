package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberProfileResponse(
	Long memberId,
	String name,
	String nickname,
	String email,
	String username,
	String profileImageUrl,
	MemberStatus status,
	MemberRole role
) {
	public static MemberProfileResponse from(MemberProfile profile) {
		return new MemberProfileResponse(
			profile.getMemberId(),
			profile.getName(),
			profile.getNickname(),
			profile.getEmail(),
			profile.getUsername(),
			profile.getProfileImageUrl(),
			profile.getStatus(),
			profile.getRole()
		);
	}
}
