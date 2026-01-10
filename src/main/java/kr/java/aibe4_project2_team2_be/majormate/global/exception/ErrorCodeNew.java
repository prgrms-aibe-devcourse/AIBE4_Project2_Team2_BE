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
	MEMBER_404(HttpStatus.NOT_FOUND, "MEMBER_404", "회원 정보를 찾을 수 없습니다."),

	// Interview
	INTERVIEW_404(HttpStatus.NOT_FOUND, "INTERVIEW_404", "인터뷰 신청 정보를 찾을 수 없습니다."),
	INTERVIEW_400_INVALID_STATE(HttpStatus.BAD_REQUEST, "INTERVIEW_400_INVALID_STATE", "현재 상태에서는 처리할 수 없습니다."),

	// Major role request
	MAJOR_REQUEST_404(HttpStatus.NOT_FOUND, "MAJOR_REQUEST_404", "전공자 신청 정보를 찾을 수 없습니다."),
	MAJOR_REQUEST_400_INVALID_STATUS(
		HttpStatus.BAD_REQUEST,
		"MAJOR_REQUEST_400_INVALID_STATUS",
		"현재 상태에서는 처리할 수 없습니다."
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

