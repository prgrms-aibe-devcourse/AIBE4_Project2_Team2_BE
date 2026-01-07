package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberProfileResponse(
	String name,
	String nickname,
	String email,
	String username,
	String profileImageUrl,
	MemberStatus status,
	String university,
	String major
) {
}
