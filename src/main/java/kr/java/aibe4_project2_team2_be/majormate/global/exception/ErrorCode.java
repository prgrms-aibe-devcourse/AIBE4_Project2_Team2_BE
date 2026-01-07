package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// Common
	INTERNAL_SERVER_ERROR("COMMON_001", "내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	INVALID_INPUT_VALUE("COMMON_002", "잘못된 입력 값입니다.", HttpStatus.BAD_REQUEST),
	METHOD_NOT_ALLOWED("COMMON_003", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
	ENTITY_NOT_FOUND("COMMON_004", "엔티티를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	ACCESS_DENIED("COMMON_005", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

	// Auth
	UNAUTHORIZED("AUTH_001", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
	INVALID_TOKEN("AUTH_002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EXPIRED_TOKEN("AUTH_003", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	REFRESH_TOKEN_NOT_FOUND("AUTH_004", "리프레시 토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
	INVALID_PASSWORD("AUTH_005", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

	// Auth (OAuth2)
	OAUTH2_PROVIDER_NOT_SUPPORTED("AUTH_006", "지원하지 않는 OAuth2 제공자입니다.", HttpStatus.BAD_REQUEST),
	OAUTH2_EMAIL_NOT_FOUND("AUTH_007", "OAuth2 제공자로부터 이메일을 받을 수 없습니다.", HttpStatus.BAD_REQUEST),
	OAUTH2_AUTHENTICATION_FAILED("AUTH_008", "OAuth2 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
	SOCIAL_LOGIN_REQUIRED("AUTH_009", "소셜 로그인이 필요합니다.", HttpStatus.BAD_REQUEST),

	// Member
	MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	DUPLICATE_NICKNAME("MEMBER_002", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
	DUPLICATE_EMAIL("MEMBER_003", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
	DUPLICATE_USERNAME("MEMBER_004", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT),
	INVALID_MEMBER_STATUS("MEMBER_005", "유효하지 않은 회원 상태입니다.", HttpStatus.BAD_REQUEST),
	MEMBER_ACADEMIC_NOT_FOUND("MEMBER_006", "회원 학적 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	CURRENT_PASSWORD_REQUIRED("MEMBER_007", "현재 비밀번호를 입력해 주세요.", HttpStatus.BAD_REQUEST),
	SAME_AS_OLD_PASSWORD("MEMBER_008", "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
	INVALID_PROFILE_IMAGE_URL("MEMBER_009", "유효하지 않은 프로필 이미지 URL입니다.", HttpStatus.BAD_REQUEST),

	// Major_Profile
	PROFILE_NOT_FOUND("PROFILE_001", "프로필을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	PROFILE_ALREADY_EXISTS("PROFILE_002", "이미 프로필이 존재합니다.", HttpStatus.CONFLICT),

	// Major_Card
	CARD_NOT_FOUND("CARD_001", "카드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	CARD_ALREADY_LIKED("CARD_002", "이미 좋아요한 카드입니다.", HttpStatus.CONFLICT),
	CARD_NOT_LIKED("CARD_003", "좋아요하지 않은 카드입니다.", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED_CARD_ACCESS("CARD_004", "카드에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

	// Question & Answer
	QUESTION_NOT_FOUND("QNA_001", "질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	ANSWER_NOT_FOUND("QNA_002", "답변을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	ANSWER_ALREADY_EXISTS("QNA_003", "이미 답변이 존재합니다.", HttpStatus.CONFLICT),
	UNAUTHORIZED_QUESTION_ACCESS("QNA_004", "질문에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
	UNAUTHORIZED_ANSWER_ACCESS("QNA_005", "답변에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

	// Interview
	INTERVIEW_NOT_FOUND("INTERVIEW_001", "인터뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	INTERVIEW_FULL("INTERVIEW_002", "인터뷰 신청 인원이 마감되었습니다.", HttpStatus.BAD_REQUEST),
	INTERVIEW_CLOSED("INTERVIEW_003", "마감된 인터뷰입니다.", HttpStatus.BAD_REQUEST),
	APPLICATION_NOT_FOUND("INTERVIEW_004", "인터뷰 신청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	APPLICATION_ALREADY_EXISTS("INTERVIEW_005", "이미 신청한 인터뷰입니다.", HttpStatus.CONFLICT),
	UNAUTHORIZED_INTERVIEW_ACCESS("INTERVIEW_006", "인터뷰에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
	INTERVIEW_TARGET_NOT_MAJOR("INTERVIEW_007", "인터뷰 신청 대상이 전공자가 아닙니다.", HttpStatus.BAD_REQUEST),
	INTERVIEW_SELF_REQUEST_NOT_ALLOWED("INTERVIEW_008", "본인에게 인터뷰를 신청할 수 없습니다.", HttpStatus.BAD_REQUEST),
	INTERVIEW_REQUEST_EMPTY("INTERVIEW_009", "신청한 인터뷰가 없습니다.", HttpStatus.NOT_FOUND),

	// Review
	REVIEW_NOT_FOUND("REVIEW_001", "후기를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	REVIEW_ALREADY_EXISTS("REVIEW_002", "이미 후기를 작성했습니다.", HttpStatus.CONFLICT),
	UNAUTHORIZED_REVIEW_ACCESS("REVIEW_003", "후기에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
	INVALID_RATING("REVIEW_004", "유효하지 않은 평점입니다. (1-5)", HttpStatus.BAD_REQUEST),

	// Major Role Request
	REQUEST_NOT_FOUND("REQUEST_001", "신청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	REQUEST_ALREADY_EXISTS("REQUEST_002", "이미 승인 대기 중인 신청이 있습니다.", HttpStatus.CONFLICT),
	REQUEST_ALREADY_PROCESSED("REQUEST_003", "이미 처리된 신청입니다.", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED_REQUEST_ACCESS("REQUEST_004", "신청에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),
	INVALID_REQUEST_STATUS("REQUEST_005", "유효하지 않은 신청 상태입니다.", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;
}
