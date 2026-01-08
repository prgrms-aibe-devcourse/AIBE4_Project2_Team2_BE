package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(
	@Min(1) @Max(5) int rating,
	@NotBlank String content
) {
}
