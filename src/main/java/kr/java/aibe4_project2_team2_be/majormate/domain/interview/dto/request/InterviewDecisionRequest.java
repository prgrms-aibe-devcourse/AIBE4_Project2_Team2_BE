package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InterviewDecisionRequest(
	@NotBlank(message = "메시지는 필수입니다.")
	@Size(max = 500, message = "메시지는 500자 이하여야 합니다.")
	String message
) {
}
