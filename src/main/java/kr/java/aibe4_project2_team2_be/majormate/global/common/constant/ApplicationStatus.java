package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {

	PENDING("대기"),
	ACCEPTED("승인"),
	REJECTED("반려"),
	CANCELLED("취소"),
	RESUBMITTED("재신청");

	private final String description;
}
