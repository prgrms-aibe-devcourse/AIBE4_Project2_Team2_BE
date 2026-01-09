package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberAcademicResponse(
	Long memberId,
	MemberStatus status,
	String university,
	String major
) {
	public static MemberAcademicResponse from(MemberAcademic academic) {
		return new MemberAcademicResponse(
			academic.getMemberProfile().getMemberId(),
			academic.getMemberProfile().getStatus(),
			academic.getUniversity(),
			academic.getMajor()
		);
	}
}
