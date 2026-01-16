package kr.java.aibe4_project2_team2_be.majormate.global.common.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private final String code;
	private final String message;
	private final Object details;

	private ErrorResponse(String code, String message, Object details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(
			errorCode.getCode(),
			errorCode.getMessage(),
			null
		);
	}

	public static ErrorResponse of(ErrorCode errorCode, Object details) {
		return new ErrorResponse(
			errorCode.getCode(),
			errorCode.getMessage(),
			details
		);
	}

	public static ErrorResponse validation(ErrorCode errorCode, List<FieldError> fieldErrors) {
		return new ErrorResponse(
			errorCode.getCode(),
			errorCode.getMessage(),
			fieldErrors
		);
	}

	@Getter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class FieldError {
		private final String field;
		private final String reason;

		private FieldError(String field, String reason) {
			this.field = field;
			this.reason = reason;
		}

		public static FieldError of(String field, String reason) {
			return new FieldError(field, reason);
		}
	}
}
