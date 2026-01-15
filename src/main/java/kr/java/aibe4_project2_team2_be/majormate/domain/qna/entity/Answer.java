package kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "question_id", nullable = false, unique = true)
	private Question question;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	private Answer(String content) {
		this.content = content;
	}

	public static Answer create(Question question, String content) {
		if (question == null) {
			throw new BusinessException(ErrorCode.QNA_400_QUESTION_REQUIRED);
		}
		if (content == null || content.isBlank()) {
			throw new BusinessException(ErrorCode.QNA_400_CONTENT_REQUIRED);
		}
		Answer answer = new Answer(content);
		question.attachAnswer(answer);
		return answer;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	void attachQuestion(Question question) {
		this.question = question;
	}
}
