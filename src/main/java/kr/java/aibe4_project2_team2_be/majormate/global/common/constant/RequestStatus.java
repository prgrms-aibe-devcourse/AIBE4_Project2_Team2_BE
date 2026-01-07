package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStatus {

	PENDING("대기"),
	RESUBMITTED("재신청"),
	APPROVED("승인"),
	REJECTED("반려");

	private final String description;
}
