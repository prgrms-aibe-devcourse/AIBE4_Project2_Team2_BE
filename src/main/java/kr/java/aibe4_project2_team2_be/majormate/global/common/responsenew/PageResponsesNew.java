package kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew;

import java.util.List;

import org.springframework.data.domain.Page;

public final class PageResponsesNew {

	private PageResponsesNew() {
	}

	public static <T> ApiResponseNew<List<T>> of(Page<T> page) {
		return ApiResponseNew.success(
			page.getContent(),
			PageMetaNew.of(
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isFirst(),
				page.isLast(),
				page.hasNext(),
				page.hasPrevious()
			)
		);
	}

	public static <T> ApiResponseNew<List<T>> of(Page<T> page, Object extraMeta) {
		return ApiResponseNew.success(
			page.getContent(),
			new CombinedMetaNew(
				PageMetaNew.of(
					page.getNumber(),
					page.getSize(),
					page.getTotalElements(),
					page.getTotalPages(),
					page.isFirst(),
					page.isLast(),
					page.hasNext(),
					page.hasPrevious()
				),
				extraMeta
			)
		);
	}

	public record CombinedMetaNew(PageMetaNew page, Object extra) {
	}
}
