package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewFormStatus {

	PENDING("대기"),
	ACCEPTED("수락"),
	REJECTED("거절"),
	COMPLETED("완료");

	private final String description;
}
