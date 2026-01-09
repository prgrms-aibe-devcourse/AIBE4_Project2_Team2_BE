package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterviewFormCreateRequest(
	@NotBlank
	String title,

	@NotBlank
	String content,

	@NotBlank
	String interviewMethod,

	@NotNull
	LocalDateTime preferredDatetime,

	String extraDescription
) {
}
