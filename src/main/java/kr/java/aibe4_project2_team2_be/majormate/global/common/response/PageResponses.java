package kr.java.aibe4_project2_team2_be.majormate.global.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

public final class PageResponses {

	private PageResponses() {
	}

	public static <T> ApiResponse<List<T>> of(Page<T> page) {
		return ApiResponse.success(
			page.getContent(),
			PageMeta.of(
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

	public static <T> ApiResponse<List<T>> of(Page<T> page, Object extraMeta) {
		return ApiResponse.success(
			page.getContent(),
			new CombinedMetaNew(
				PageMeta.of(
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

	public record CombinedMetaNew(PageMeta page, Object extra) {
	}
}
