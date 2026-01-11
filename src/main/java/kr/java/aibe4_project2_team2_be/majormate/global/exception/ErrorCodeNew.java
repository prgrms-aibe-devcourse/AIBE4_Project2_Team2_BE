package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCodeNew {

	// Common
	COMMON_400(HttpStatus.BAD_REQUEST, "COMMON_400", "요청 값이 올바르지 않습니다."),
	COMMON_401(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
	COMMON_403(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
	COMMON_404(HttpStatus.NOT_FOUND, "COMMON_404", "리소스를 찾을 수 없습니다."),
	COMMON_409(HttpStatus.CONFLICT, "COMMON_409", "요청이 현재 상태와 충돌합니다."),
	COMMON_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다."),

	// Auth
	AUTH_401(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."),
	AUTH_403(HttpStatus.FORBIDDEN, "AUTH_403", "접근 권한이 없습니다."),

	// Member
	MEMBER_400_PASSWORD_REQUIRED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_PASSWORD_REQUIRED", "로컬 회원가입은 비밀번호가 필수입니다."
	),
	MEMBER_400_CURRENT_PASSWORD_REQUIRED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_CURRENT_PASSWORD_REQUIRED", "로컬 계정은 정보 변경 시 현재 비밀번호 입력이 필요합니다."
	),
	MEMBER_400_CURRENT_PASSWORD_MISMATCH(
		HttpStatus.BAD_REQUEST, "MEMBER_400_CURRENT_PASSWORD_MISMATCH", "현재 비밀번호가 일치하지 않습니다."
	),
	MEMBER_400_INVALID_AUTH_PROVIDER(
		HttpStatus.BAD_REQUEST, "MEMBER_400_INVALID_AUTH_PROVIDER", "소셜 가입에는 LOCAL provider를 사용할 수 없습니다."
	),
	MEMBER_400_CURRENT_PASSWORD_NOT_ALLOWED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_CURRENT_PASSWORD_NOT_ALLOWED", "소셜 계정은 currentPassword를 전송할 수 없습니다."
	),
	MEMBER_400_PASSWORD_CHANGE_NOT_ALLOWED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_PASSWORD_CHANGE_NOT_ALLOWED", "소셜 계정은 비밀번호를 변경할 수 없습니다."
	),
	MEMBER_400_SAME_AS_OLD_PASSWORD(
		HttpStatus.BAD_REQUEST, "MEMBER_400_SAME_AS_OLD_PASSWORD", "새 비밀번호는 기존 비밀번호와 다르게 설정해야 합니다."
	),
	MEMBER_409_DUPLICATE_NICKNAME(
		HttpStatus.CONFLICT, "MEMBER_409_DUPLICATE_NICKNAME", "이미 사용 중인 닉네임입니다."
	),
	MEMBER_409_DUPLICATE_EMAIL(
		HttpStatus.CONFLICT, "MEMBER_409_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."
	),
	MEMBER_400_INVALID_ROLE_TRANSITION(
		HttpStatus.BAD_REQUEST, "MEMBER_400_INVALID_ROLE_TRANSITION", "역할 변경 요청이 올바르지 않습니다."
	),
	MEMBER_400_ROLE_CHANGE_NOT_ALLOWED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_ROLE_CHANGE_NOT_ALLOWED", "해당 역할은 변경할 수 없습니다."
	),
	MEMBER_404(
		HttpStatus.NOT_FOUND, "MEMBER_404", "회원 정보를 찾을 수 없습니다."
	),

	// Interview
	INTERVIEW_404(HttpStatus.NOT_FOUND, "INTERVIEW_404", "인터뷰 신청 정보를 찾을 수 없습니다."),
	INTERVIEW_400_INVALID_STATE(HttpStatus.BAD_REQUEST, "INTERVIEW_400_INVALID_STATE", "현재 상태에서는 처리할 수 없습니다."),

	// Major role request
	MAJOR_REQUEST_400_STATUS_REQUIRED(
		HttpStatus.BAD_REQUEST, "MAJOR_REQUEST_400_STATUS_REQUIRED", "전공자 신청을 위해 신분 정보가 필요합니다."
	),
	MAJOR_REQUEST_400_ACADEMIC_REQUIRED(
		HttpStatus.BAD_REQUEST, "MAJOR_REQUEST_400_ACADEMIC_REQUIRED", "전공자 신청을 위해 대학교와 학과 정보가 필요합니다."
	),
	MAJOR_REQUEST_400_INVALID_STATUS(
		HttpStatus.BAD_REQUEST, "MAJOR_REQUEST_400_INVALID_STATUS", "전공자 신청은 재학생 또는 졸업생만 가능합니다."
	),
	MAJOR_REQUEST_404(
		HttpStatus.NOT_FOUND, "MAJOR_REQUEST_404", "전공자 신청 정보를 찾을 수 없습니다."
	),

	// Major
	MAJOR_400_ACADEMIC_REQUIRED(
		HttpStatus.BAD_REQUEST, "MAJOR_400_ACADEMIC_REQUIRED", "전공자(MAJOR)는 대학교와 학과 정보가 필수입니다."
	);

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	ErrorCodeNew(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	private static final Map<String, ErrorCodeNew> CODE_MAP =
		Arrays.stream(values()).collect(Collectors.toMap(ErrorCodeNew::getCode, Function.identity()));

	public static ErrorCodeNew fromCode(String code) {
		ErrorCodeNew result = CODE_MAP.get(code);
		return result != null ? result : COMMON_500;
	}
}

