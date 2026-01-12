package kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseNew<T> {

	private final boolean success;
	private final T data;
	private final Object meta;
	private final ErrorResponseNew error;

	private ApiResponseNew(boolean success, T data, Object meta, ErrorResponseNew error) {
		this.success = success;
		this.data = data;
		this.meta = meta;
		this.error = error;
	}

	public static <T> ApiResponseNew<T> success() {
		return new ApiResponseNew<>(true, null, null, null);
	}

	public static <T> ApiResponseNew<T> success(T data) {
		return new ApiResponseNew<>(true, data, null, null);
	}

	public static <T> ApiResponseNew<T> success(T data, Object meta) {
		return new ApiResponseNew<>(true, data, meta, null);
	}

	public static <T> ApiResponseNew<T> error(ErrorResponseNew error) {
		return new ApiResponseNew<>(false, null, null, error);
	}
}

