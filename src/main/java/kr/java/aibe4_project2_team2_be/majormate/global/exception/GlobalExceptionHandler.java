package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
		log.error("BusinessException: {}", e.getMessage(), e);
		ErrorCode errorCode = e.getErrorCode();
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
		List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> ErrorResponse.FieldError.of(
				error.getField(),
				error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
				error.getDefaultMessage()
			))
			.collect(Collectors.toList());

		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(BindException.class)
	protected ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
		log.error("BindException: {}", e.getMessage(), e);
		List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> ErrorResponse.FieldError.of(
				error.getField(),
				error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
				error.getDefaultMessage()
			))
			.collect(Collectors.toList());

		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException: {}", e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e
	) {
		log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);
		Throwable cause = e.getCause();
		if (cause instanceof InvalidFormatException ife) {
			Class<?> targetType = ife.getTargetType();
			if (MemberStatus.class.equals(targetType)) {
				ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_MEMBER_STATUS);
				return ResponseEntity
					.status(ErrorCode.INVALID_MEMBER_STATUS.getHttpStatus())
					.body(ApiResponse.error(errorResponse));
			}
		}

		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(errorResponse));
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("Exception: {}", e.getMessage(), e);
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error(errorResponse));
	}
}
