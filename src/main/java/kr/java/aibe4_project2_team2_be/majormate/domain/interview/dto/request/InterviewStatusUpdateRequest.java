package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;

public record InterviewStatusUpdateRequest(
	@NotNull(message = "변경할 인터뷰 상태는 필수입니다.")
	InterviewFormStatus status,

	@Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
	String majorMessage
) {
}
