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
	RESUBMITTED("재신청"),
    REVOKED("자격 박탈"),   // 형민
    SUSPENSION_REQUESTED("활동 정지 요청");

	private final String description;
}
