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

	private Answer(Question question, String content) {
		this.question = question;
		this.content = content;
	}

	public static Answer create(Question question, String content) {
		return new Answer(question, content);
	}

	public void updateContent(String content) {
		this.content = content;
	}
}
