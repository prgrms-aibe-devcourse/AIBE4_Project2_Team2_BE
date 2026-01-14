package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response;

import java.time.LocalDateTime;

public record QuestionMyItemResponse(
	Long questionId,
	Long majorMemberId,
	String questionContent,
	boolean hasAnswer,
	AnswerSummary answer,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public record AnswerSummary(
		Long answerId,
		String content,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
	}
}
