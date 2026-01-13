package kr.java.aibe4_project2_team2_be.majormate.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 20;
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
	);

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		// null이거나 빈 문자열이면 검증 통과 (선택적 필드)
		if (password == null || password.isBlank()) {
			return true;
		}

		// 길이 검증
		if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
				String.format("비밀번호는 %d~%d자여야 합니다.", MIN_LENGTH, MAX_LENGTH)
			).addConstraintViolation();
			return false;
		}

		// 패턴 검증 (영문, 숫자, 특수문자 포함)
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
				"비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
			).addConstraintViolation();
			return false;
		}

		return true;
	}
}
