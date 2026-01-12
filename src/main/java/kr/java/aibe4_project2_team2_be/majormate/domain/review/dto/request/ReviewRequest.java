package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
	@NotNull(message = "평점은 필수입니다.")
	@Min(value = 1, message = "평점은 1~5 사이여야 합니다.")
	@Max(value = 5, message = "평점은 1~5 사이여야 합니다.")
	Integer rating,

	@NotBlank(message = "후기 내용은 필수입니다.")
	@Size(max = 1000, message = "후기 내용은 1000자 이하여야 합니다.")
	String content
) {
}
