package kr.java.aibe4_project2_team2_be.majormate.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageMeta(
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean first,
	boolean last,
	boolean hasNext,
	boolean hasPrevious
) {
	public static PageMeta of(
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last,
		boolean hasNext,
		boolean hasPrevious
	) {
		return new PageMeta(page, size, totalElements, totalPages, first, last, hasNext, hasPrevious);
	}
}
