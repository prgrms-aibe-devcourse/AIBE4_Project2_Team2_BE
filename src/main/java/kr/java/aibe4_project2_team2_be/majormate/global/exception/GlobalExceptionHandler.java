package kr.java.aibe4_project2_team2_be.majormate.global.exception;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolationException;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ErrorResponseNew;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleBusinessException(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("BusinessExceptionNew: code={}, message={}", errorCode.getCode(), e.getMessage(), e);

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiResponseNew.error(ErrorResponseNew.of(errorCode)));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.warn("MethodArgumentNotValidException: {}", e.getMessage(), e);

		List<ErrorResponseNew.FieldError> details = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(err -> ErrorResponseNew.FieldError.of(err.getField(),
				Optional.ofNullable(err.getDefaultMessage()).orElse("요청 값이 올바르지 않습니다.")))
			.toList();

		return badRequestWithDetails(details);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleBindException(BindException e) {
		log.warn("BindException: {}", e.getMessage(), e);

		List<ErrorResponseNew.FieldError> details = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(err -> ErrorResponseNew.FieldError.of(err.getField(),
				Optional.ofNullable(err.getDefaultMessage()).orElse("요청 값이 올바르지 않습니다.")))
			.toList();

		return badRequestWithDetails(details);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleConstraintViolationException(ConstraintViolationException e) {
		log.warn("ConstraintViolationException: {}", e.getMessage(), e);

		List<ErrorResponseNew.FieldError> details = e.getConstraintViolations()
			.stream()
			.map(v -> ErrorResponseNew.FieldError.of(
				v.getPropertyPath() == null ? "parameter" : v.getPropertyPath().toString(),
				Optional.ofNullable(v.getMessage()).orElse("요청 값이 올바르지 않습니다.")
			))
			.toList();

		return badRequestWithDetails(details);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage(), e);
		return badRequest();
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		log.warn("MissingServletRequestParameterException: {}", e.getMessage(), e);
		return badRequest();
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e) {
		log.warn("HttpMessageNotReadableException: {}", e.getMessage(), e);

		Throwable cause = e.getCause();
		if (cause instanceof InvalidFormatException ife) {
			if (isEnumTargetType(ife)) {
				return badRequest();
			}
		}

		return badRequest();
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleAuthenticationException(AuthenticationException e) {
		log.warn("AuthenticationException: {}", e.getMessage(), e);
		return ResponseEntity
			.status(ErrorCode.AUTH_401.getHttpStatus())
			.body(ApiResponseNew.error(ErrorResponseNew.of(ErrorCode.AUTH_401)));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponseNew<Void>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("AccessDeniedException: {}", e.getMessage(), e);
		return ResponseEntity
			.status(ErrorCode.AUTH_403.getHttpStatus())
			.body(ApiResponseNew.error(ErrorResponseNew.of(ErrorCode.AUTH_403)));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponseNew<Void>> handleException(Exception e) {
		log.error("Unhandled Exception: {}", e.getMessage(), e);
		return ResponseEntity
			.status(ErrorCode.COMMON_500.getHttpStatus())
			.body(ApiResponseNew.error(ErrorResponseNew.of(ErrorCode.COMMON_500)));
	}

	private ResponseEntity<ApiResponseNew<Void>> badRequest() {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponseNew.error(ErrorResponseNew.of(ErrorCode.COMMON_400)));
	}

	private ResponseEntity<ApiResponseNew<Void>> badRequestWithDetails(Object details) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponseNew.error(ErrorResponseNew.of(ErrorCode.COMMON_400, details)));
	}

	private boolean isEnumTargetType(InvalidFormatException ife) {
		Class<?> targetType = ife.getTargetType();
		if (targetType != null && targetType.isEnum()) {
			return true;
		}

		for (JsonMappingException.Reference ref : ife.getPath()) {
			if (ref != null && ref.getFrom() != null) {
				Class<?> fromType = ref.getFrom().getClass();
				if (fromType.isEnum()) {
					return true;
				}
			}
		}
		return false;
	}
}
