package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberInfoResponse(
	Long memberId,
	String name,
	String nickname,
	String email,
	String username,
	String profileImageUrl,
	MemberStatus status,
	String university,
	String major,
	MemberRole role,
	boolean isLocal
) {
	public static MemberInfoResponse from(MemberProfile profile) {
		MemberAcademic academic = profile.getAcademic();
		return new MemberInfoResponse(
			profile.getMemberId(),
			profile.getName(),
			profile.getNickname(),
			profile.getEmail(),
			profile.getUsername(),
			profile.getProfileImageUrl(),
			profile.getStatus(),
			academic != null ? academic.getUniversity() : null,
			academic != null ? academic.getMajor() : null,
			profile.getRole(),
			profile.isLocalUser()
		);
	}
}
