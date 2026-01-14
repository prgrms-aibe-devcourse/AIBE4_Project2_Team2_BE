package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.Objects;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(require(errorCode).getMessage());
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = require(errorCode);
	}

	public BusinessException(ErrorCode errorCode, Throwable cause) {
		super(require(errorCode).getMessage(), cause);
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = require(errorCode);
	}

	private static ErrorCode require(ErrorCode errorCode) {
		return Objects.requireNonNull(errorCode, "errorCode must not be null");
	}
}
