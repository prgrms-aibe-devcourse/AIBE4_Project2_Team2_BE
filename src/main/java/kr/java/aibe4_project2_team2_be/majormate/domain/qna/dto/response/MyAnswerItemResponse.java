package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response;

import java.time.LocalDateTime;

public record MyAnswerItemResponse(
	Long answerId,
	Long questionId,
	Long studentMemberId,
	String questionContent,
	String answerContent,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
}
