package kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageMetaNew(
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean first,
	boolean last,
	boolean hasNext,
	boolean hasPrevious
) {
	public static PageMetaNew of(
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last,
		boolean hasNext,
		boolean hasPrevious
	) {
		return new PageMetaNew(page, size, totalElements, totalPages, first, last, hasNext, hasPrevious);
	}
}
