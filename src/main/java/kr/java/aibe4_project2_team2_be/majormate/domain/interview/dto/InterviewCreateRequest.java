package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterviewCreateRequest(
	@NotBlank String title,
	@NotBlank String content,
	@NotNull LocalDateTime preferredDatetime,
	@NotBlank String interviewMethod,
	String extraDescription
) {
}

