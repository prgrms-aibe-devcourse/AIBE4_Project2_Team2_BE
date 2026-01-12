package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;

public record MemberInfoUpdateRequest(
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
	@Pattern(
		regexp = "^[가-힣a-zA-Z0-9_-]{2,20}$",
		message = "닉네임은 한글, 영문, 숫자, 밑줄, 하이픈만 사용 가능합니다."
	)
	String nickname,

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
	String email,

	@Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	String currentPassword,

	@Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
	@Pattern(
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
		message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
	)
	String newPassword,

	MemberStatus status,

	@Size(max = 20, message = "대학교명은 20자 이하여야 합니다.")
	String university,

	@Size(max = 20, message = "학과명은 20자 이하여야 합니다.")
	String major
) {
}
