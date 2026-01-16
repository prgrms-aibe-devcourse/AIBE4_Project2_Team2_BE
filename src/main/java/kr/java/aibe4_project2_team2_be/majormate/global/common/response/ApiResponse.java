package kr.java.aibe4_project2_team2_be.majormate.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final boolean success;
	private final T data;
	private final Object meta;
	private final ErrorResponse error;

	private ApiResponse(boolean success, T data, Object meta, ErrorResponse error) {
		this.success = success;
		this.data = data;
		this.meta = meta;
		this.error = error;
	}

	public static <T> ApiResponse<T> success() {
		return new ApiResponse<>(true, null, null, null);
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null, null);
	}

	public static <T> ApiResponse<T> success(T data, Object meta) {
		return new ApiResponse<>(true, data, meta, null);
	}

	public static <T> ApiResponse<T> error(ErrorResponse error) {
		return new ApiResponse<>(false, null, null, error);
	}
}

