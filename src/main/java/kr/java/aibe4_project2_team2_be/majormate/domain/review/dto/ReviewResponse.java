package kr.java.aibe4_project2_team2_be.majormate.domain.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
	Long reviewId,
	Long interviewId,

	MajorSummary major,

	int rating,
	String content,
	LocalDateTime createdAt
) {
	public record MajorSummary(
		String profileImageUrl, // nullable
		String nickname,
		String university,
		String major
	) {
	}
}
