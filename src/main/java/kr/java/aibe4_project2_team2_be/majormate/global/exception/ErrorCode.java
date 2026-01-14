package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// Common
	COMMON_400(
		HttpStatus.BAD_REQUEST, "COMMON_400", "요청 값이 올바르지 않습니다."
	),
	COMMON_401(
		HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."
	),
	COMMON_403(
		HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."
	),
	COMMON_404(
		HttpStatus.NOT_FOUND, "COMMON_404", "리소스를 찾을 수 없습니다."
	),
	COMMON_405_METHOD_NOT_ALLOWED(
		HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405_METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."
	),
	COMMON_409(
		HttpStatus.CONFLICT, "COMMON_409", "요청이 현재 상태와 충돌합니다."
	),
	COMMON_500(
		HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 오류가 발생했습니다."
	),
	COMMON_500_SNAPSHOT_MISSING(
		HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_SNAPSHOT_MISSING", "요청 처리에 필요한 스냅샷 데이터가 없습니다."
	),

	// Auth
	AUTH_401(
		HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."
	),
	AUTH_401_INVALID_TOKEN(
		HttpStatus.UNAUTHORIZED, "AUTH_401_INVALID_TOKEN", "유효하지 않은 토큰입니다."
	),
	AUTH_401_EXPIRED_TOKEN(
		HttpStatus.UNAUTHORIZED, "AUTH_401_EXPIRED_TOKEN", "만료된 토큰입니다."
	),
	AUTH_401_REFRESH_TOKEN_NOT_FOUND(
		HttpStatus.UNAUTHORIZED, "AUTH_401_REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰을 찾을 수 없습니다."
	),
	AUTH_401_INVALID_PASSWORD(
		HttpStatus.UNAUTHORIZED, "AUTH_401_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."
	),
	AUTH_400_EMAIL_NOT_VERIFIED(
		HttpStatus.BAD_REQUEST, "AUTH_400_EMAIL_NOT_VERIFIED", "이메일 인증이 필요합니다."
	),
	AUTH_400_SOCIAL_LOGIN_REQUIRED(
		HttpStatus.BAD_REQUEST, "AUTH_400_SOCIAL_LOGIN_REQUIRED", "소셜 로그인이 필요합니다."
	),
	AUTH_400_OAUTH2_EMAIL_NOT_FOUND(
		HttpStatus.BAD_REQUEST, "AUTH_400_OAUTH2_EMAIL_NOT_FOUND", "OAuth2 제공자로부터 이메일을 받을 수 없습니다."
	),
	AUTH_400_OAUTH2_PROVIDER_NOT_SUPPORTED(
		HttpStatus.BAD_REQUEST, "AUTH_400_OAUTH2_PROVIDER_NOT_SUPPORTED", "지원하지 않는 OAuth2 제공자입니다."
	),
	AUTH_400_VERIFICATION_CODE_NOT_FOUND(
		HttpStatus.BAD_REQUEST, "AUTH_400_VERIFICATION_CODE_NOT_FOUND", "인증 코드를 찾을 수 없습니다."
	),
	AUTH_400_VERIFICATION_CODE_EXPIRED(
		HttpStatus.BAD_REQUEST, "AUTH_400_VERIFICATION_CODE_EXPIRED", "인증 코드가 만료되었습니다."
	),
	AUTH_400_INVALID_VERIFICATION_CODE(
		HttpStatus.BAD_REQUEST, "AUTH_400_INVALID_VERIFICATION_CODE", "인증 코드가 일치하지 않습니다."
	),
	AUTH_400_ALREADY_VERIFIED(
		HttpStatus.BAD_REQUEST, "AUTH_400_ALREADY_VERIFIED", "이미 인증되었습니다."
	),
	AUTH_500_EMAIL_SEND_FAILED(
		HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_500_EMAIL_SEND_FAILED", "이메일 발송에 실패했습니다."
	),
	AUTH_403(
		HttpStatus.FORBIDDEN, "AUTH_403", "접근 권한이 없습니다."
	),

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
	MEMBER_409_DUPLICATE_USERNAME(
		HttpStatus.CONFLICT, "MEMBER_409_DUPLICATE_USERNAME", "이미 사용 중인 아이디입니다."
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

	// Member - Profile Image
	MEMBER_400_PROFILE_IMAGE_FILE_REQUIRED(
		HttpStatus.BAD_REQUEST, "MEMBER_400_PROFILE_IMAGE_FILE_REQUIRED", "프로필 이미지 파일은 필수입니다."
	),

	// Interview
	INTERVIEW_404(
		HttpStatus.NOT_FOUND, "INTERVIEW_404", "인터뷰 신청 정보를 찾을 수 없습니다."
	),
	INTERVIEW_400_SELF_REQUEST_NOT_ALLOWED(
		HttpStatus.BAD_REQUEST, "INTERVIEW_400_SELF_REQUEST_NOT_ALLOWED", "자기 자신에게 인터뷰를 신청할 수 없습니다."
	),
	INTERVIEW_400_TARGET_NOT_MAJOR(
		HttpStatus.BAD_REQUEST, "INTERVIEW_400_TARGET_NOT_MAJOR", "인터뷰 대상은 전공자여야 합니다."
	),
	INTERVIEW_400_INVALID_STATE(
		HttpStatus.BAD_REQUEST, "INTERVIEW_400_INVALID_STATE", "현재 상태에서는 요청을 처리할 수 없습니다."
	),
	INTERVIEW_409_ALREADY_EXISTS(
		HttpStatus.CONFLICT, "INTERVIEW_409_ALREADY_EXISTS", "진행 중인 인터뷰 신청이 이미 존재합니다."
	),
	INTERVIEW_500_SNAPSHOT_MISSING(
		HttpStatus.INTERNAL_SERVER_ERROR, "INTERVIEW_500_SNAPSHOT_MISSING", "인터뷰 스냅샷 데이터가 존재하지 않습니다."
	),

	// Review
	REVIEW_400_INTERVIEW_NOT_COMPLETED(
		HttpStatus.BAD_REQUEST, "REVIEW_400_INTERVIEW_NOT_COMPLETED", "완료된 인터뷰에 대해서만 후기를 작성할 수 있습니다."
	),
	REVIEW_403_NOT_OWNER(
		HttpStatus.FORBIDDEN, "REVIEW_403_NOT_OWNER", "본인이 작성할 수 있는 후기가 아닙니다."
	),
	REVIEW_403_NOT_RECEIVER(
		HttpStatus.FORBIDDEN, "REVIEW_403_NOT_RECEIVER", "본인이 받은 후기가 아닙니다."
	),
	REVIEW_404(
		HttpStatus.NOT_FOUND, "REVIEW_404", "후기 정보를 찾을 수 없습니다."
	),
	REVIEW_409_ALREADY_EXISTS(
		HttpStatus.CONFLICT, "REVIEW_409_ALREADY_EXISTS", "이미 해당 인터뷰에 대한 후기가 존재합니다."
	),

	// QnA
	QNA_404_QUESTION(
		HttpStatus.NOT_FOUND, "QNA_404_QUESTION", "질문을 찾을 수 없습니다."
	),
	QNA_404_ANSWER(
		HttpStatus.NOT_FOUND, "QNA_404_ANSWER", "답변을 찾을 수 없습니다."
	),
	QNA_400_ALREADY_ANSWERED(
		HttpStatus.BAD_REQUEST, "QNA_400_ALREADY_ANSWERED", "답변이 달린 질문은 수정하거나 삭제할 수 없습니다."
	),
	QNA_409_ANSWER_ALREADY_EXISTS(
		HttpStatus.CONFLICT, "QNA_409_ANSWER_ALREADY_EXISTS", "이미 답변이 존재합니다."
	),
	QNA_400_TARGET_NOT_MAJOR(
		HttpStatus.BAD_REQUEST, "QNA_400_TARGET_NOT_MAJOR", "질문 대상이 전공자가 아닙니다."
	),
	QNA_400_SELF_QUESTION_NOT_ALLOWED(
		HttpStatus.BAD_REQUEST, "QNA_400_SELF_QUESTION_NOT_ALLOWED", "자기 자신에게 질문할 수 없습니다."
	),

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
	MAJOR_ROLE_REQUEST_403_NOT_OWNER(
		HttpStatus.FORBIDDEN, "MAJOR_ROLE_REQUEST_403_NOT_OWNER", "해당 전공자 지원서를 수정할 권한이 없습니다."
	),
	MAJOR_REQUEST_404(
		HttpStatus.NOT_FOUND, "MAJOR_REQUEST_404", "전공자 신청 정보를 찾을 수 없습니다."
	),

	// Major
	MAJOR_400_ACADEMIC_REQUIRED(
		HttpStatus.BAD_REQUEST, "MAJOR_400_ACADEMIC_REQUIRED", "전공자는 대학교와 학과 정보가 필수입니다."
	),
	MAJOR_400_STATUS_REQUIRED(
		HttpStatus.BAD_REQUEST, "MAJOR_400_STATUS_REQUIRED", "전공자 상태 정보가 없어 인터뷰 신청을 처리할 수 없습니다."
	),
	MAJOR_403_ROLE_REQUIRED(
		HttpStatus.FORBIDDEN, "MAJOR_403_ROLE_REQUIRED", "MAJOR 권한이 필요합니다."
	),
	MAJOR_404_PROFILE_REQUIRED(
		HttpStatus.NOT_FOUND, "MAJOR_404_PROFILE_REQUIRED", "프로필을 찾을 수 없습니다."
	),
	MAJOR_400_PROFILE_NOT_ACTIVE(
		HttpStatus.BAD_REQUEST, "MAJOR_400_PROFILE_NOT_ACTIVE", "비활성화 된 전공자입니다."
	),

	// File
	FILE_400_EMPTY_FILE(
		HttpStatus.BAD_REQUEST, "FILE_400_EMPTY_FILE", "빈 파일은 업로드할 수 없습니다."
	),
	FILE_400_UNSUPPORTED_CONTENT_TYPE(
		HttpStatus.BAD_REQUEST, "FILE_400_UNSUPPORTED_CONTENT_TYPE", "허용되지 않는 파일 형식입니다. (허용: jpg, png)"
	),
	FILE_400_INVALID_FILE_URL(
		HttpStatus.BAD_REQUEST, "FILE_400_INVALID_FILE_URL", "파일 URL이 올바르지 않습니다."
	),
	FILE_400_CANNOT_EXTRACT_FILE_KEY(
		HttpStatus.BAD_REQUEST, "FILE_400_CANNOT_EXTRACT_FILE_KEY", "파일 URL에서 파일 key를 추출할 수 없습니다."
	),

	FILE_500_UPLOAD_FAILED(
		HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_UPLOAD_FAILED", "파일 업로드에 실패했습니다."
	),
	FILE_500_DELETE_FAILED(
		HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_DELETE_FAILED", "파일 삭제에 실패했습니다."
	),
	FILE_500_PUBLIC_URL_BUILD_FAILED(
		HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_PUBLIC_URL_BUILD_FAILED", "파일 공개 URL 생성에 실패했습니다."
	);

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	private static final Map<String, ErrorCode> CODE_MAP =
		Arrays.stream(values()).collect(Collectors.toMap(ErrorCode::getCode, Function.identity()));

	public static ErrorCode fromCode(String code) {
		ErrorCode result = CODE_MAP.get(code);
		return result != null ? result : COMMON_500;
	}
}

