package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

	ENROLLED("재학생"),
	GRADUATED("졸업생"),
	HIGH_SCHOOL("고등학생"),
	ETC("기타");

	private final String description;
}
