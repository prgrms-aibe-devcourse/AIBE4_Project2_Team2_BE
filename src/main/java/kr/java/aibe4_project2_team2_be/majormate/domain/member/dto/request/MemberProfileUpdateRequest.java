package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.validation.ValidPassword;

public record MemberProfileUpdateRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
	@Pattern(
		regexp = "^[가-힣a-zA-Z0-9_-]{2,20}$",
		message = "닉네임은 한글, 영문, 숫자, 밑줄, 하이픈만 사용 가능합니다."
	)
	String nickname,

	@ValidPassword
	String currentPassword,

	@ValidPassword
	String newPassword,

	@NotNull(message = "신분은 필수입니다.")
	MemberStatus status,

	@Size(max = 20, message = "대학교명은 20자 이하여야 합니다.")
	String university,

	@Size(max = 20, message = "학과명은 20자 이하여야 합니다.")
	String major
) {
}
