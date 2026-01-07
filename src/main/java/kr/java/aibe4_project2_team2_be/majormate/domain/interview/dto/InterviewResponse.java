package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewStatus;

public record InterviewResponse(
	@NotNull MajorSnapshot major,
	@NotNull InterviewContent interview,
	@NotNull InterviewStatus status,
	@Nullable String majorMessage
) {

	public record MajorSnapshot(
		@Nullable String profileImageUrl,
		@NotBlank String nickname,
		@NotBlank String university,
		@NotBlank String major
	) {
	}

	public record InterviewContent(
		@NotBlank String title,
		@NotBlank String content,
		@NotBlank String interviewMethod,
		@NotNull LocalDateTime preferredDatetime,
		@Nullable String extraDescription
	) {
	}
}
