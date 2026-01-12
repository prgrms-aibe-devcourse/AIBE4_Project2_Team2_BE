package kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InterviewFormCreateRequest(
	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 255, message = "제목은 255자 이하여야 합니다.")
	String title,

	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 5000, message = "내용은 5000자 이하여야 합니다.")
	String content,

	@NotBlank(message = "인터뷰 진행 방식은 필수입니다.")
	@Size(max = 255, message = "인터뷰 진행 방식은 255자 이하여야 합니다.")
	String interviewMethod,

	@NotNull(message = "희망 인터뷰 날짜 및 시간은 필수입니다.")
	@Future(message = "희망 인터뷰 날짜 및 시간은 현재보다 이후여야 합니다.")
	LocalDateTime preferredDatetime,

	@Size(max = 2000, message = "추가 설명은 2000자 이하여야 합니다.")
	String extraDescription
) {
}
