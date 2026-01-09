package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberDetailResponse(
	Long memberId,
	String name,
	String nickname,
	String email,
	String username,
	String profileImageUrl,
	MemberStatus status,
	String university,
	String major,
	MemberRole role
) {
	public static MemberDetailResponse from(MemberProfile profile, MemberAcademic academic) {
		return new MemberDetailResponse(
			profile.getMemberId(),
			profile.getName(),
			profile.getNickname(),
			profile.getEmail(),
			profile.getUsername(),
			profile.getProfileImageUrl(),
			profile.getStatus(),
			academic.getUniversity(),
			academic.getMajor(),
			profile.getRole()
		);
	}
}
