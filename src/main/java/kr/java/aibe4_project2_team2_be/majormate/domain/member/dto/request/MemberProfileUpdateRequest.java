package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberProfileUpdateRequest(
	@NotBlank
	String nickname,

	@Email
	@NotBlank
	String email,

	@NotBlank
	String currentPassword,

	String newPassword,

	String profileImageUrl,

	@NotNull
	MemberStatus status,

	@NotBlank
	String university,

	@NotBlank
	String major
) {
}
