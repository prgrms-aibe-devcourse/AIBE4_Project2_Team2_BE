package kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response;

import java.time.LocalDateTime;

public record QuestionReceivedItemResponse(
	Long questionId,
	Long studentMemberId,
	String studentNickname,
	String content,
	boolean hasAnswer,
	String answerContent,
	LocalDateTime answerCreatedAt,
	LocalDateTime createdAt
) {
}
