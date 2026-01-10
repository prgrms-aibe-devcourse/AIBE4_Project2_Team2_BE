package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.Objects;

import lombok.Getter;

@Getter
public class BusinessExceptionNew extends RuntimeException {

	private final ErrorCodeNew errorCode;

	public BusinessExceptionNew(ErrorCodeNew errorCode) {
		super(require(errorCode).getMessage());
		this.errorCode = errorCode;
	}

	public BusinessExceptionNew(ErrorCodeNew errorCode, String message) {
		super(message);
		this.errorCode = require(errorCode);
	}

	public BusinessExceptionNew(ErrorCodeNew errorCode, Throwable cause) {
		super(require(errorCode).getMessage(), cause);
		this.errorCode = errorCode;
	}

	public BusinessExceptionNew(ErrorCodeNew errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = require(errorCode);
	}

	private static ErrorCodeNew require(ErrorCodeNew errorCode) {
		return Objects.requireNonNull(errorCode, "errorCode must not be null");
	}
}
