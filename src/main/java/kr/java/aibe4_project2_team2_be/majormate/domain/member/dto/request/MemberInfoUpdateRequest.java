package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberDetailUpdateRequest(
	@NotBlank
	@Size(min = 2, max = 50)
	@Pattern(regexp = "^[가-힣a-zA-Z0-9_-]{2,50}$")
	String nickname,

	@NotBlank
	@Email
	String email,

	@NotBlank
	@Size(min = 8, max = 20)
	String currentPassword,

	@Size(min = 8, max = 20)
	String newPassword,

	String profileImageUrl,

	String status,

	String university,

	String major
) {
}
