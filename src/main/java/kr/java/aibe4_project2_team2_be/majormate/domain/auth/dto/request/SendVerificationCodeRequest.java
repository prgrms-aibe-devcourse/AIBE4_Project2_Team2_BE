package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendVerificationCodeRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "인증 타입은 필수입니다.")
    private EmailVerification.VerificationType type;
}
