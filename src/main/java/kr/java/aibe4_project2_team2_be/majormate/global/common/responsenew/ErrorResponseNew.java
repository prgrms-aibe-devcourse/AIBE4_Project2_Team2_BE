package kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseNew {

	private final String code;
	private final String message;
	private final Object details;

	private ErrorResponseNew(String code, String message, Object details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}

	public static ErrorResponseNew of(ErrorCodeNew errorCode) {
		return new ErrorResponseNew(
			errorCode.getCode(),
			errorCode.getMessage(),
			null
		);
	}

	public static ErrorResponseNew of(ErrorCodeNew errorCode, Object details) {
		return new ErrorResponseNew(
			errorCode.getCode(),
			errorCode.getMessage(),
			details
		);
	}

	public static ErrorResponseNew validation(ErrorCodeNew errorCode, List<FieldError> fieldErrors) {
		return new ErrorResponseNew(
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
