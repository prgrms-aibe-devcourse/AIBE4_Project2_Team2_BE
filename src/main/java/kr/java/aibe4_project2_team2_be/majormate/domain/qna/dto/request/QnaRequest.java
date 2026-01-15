package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QnaRequest(
	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 3000, message = "내용은 3000자 이하여야 합니다.")
	String content
) {
}
