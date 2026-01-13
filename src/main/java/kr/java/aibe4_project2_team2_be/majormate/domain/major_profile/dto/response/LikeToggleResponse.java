package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeToggleResponse {
	private final boolean isLiked;
	private final long totalLikes;

	public static LikeToggleResponse of(boolean isLiked, long totalLikes) {
		return LikeToggleResponse.builder()
			.isLiked(isLiked)
			.totalLikes(totalLikes)
			.build();
	}
}
