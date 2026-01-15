package kr.java.aibe4_project2_team2_be.majormate.domain.qna.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_member_id", nullable = false)
	private MemberProfile student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "major_member_id", nullable = false)
	private MemberProfile major;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private boolean hasAnswer = false;

	@OneToOne(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Answer answer;

	private Question(MemberProfile student, MemberProfile major, String content, boolean hasAnswer) {
		this.student = student;
		this.major = major;
		this.content = content;
		this.hasAnswer = hasAnswer;
	}

	public static Question create(MemberProfile student, MemberProfile major, String content) {
		return new Question(student, major, content, false);
	}

	public boolean isAnswered() {
		return hasAnswer;
	}

	public boolean isEditable() {
		return !hasAnswer;
	}

	public boolean isDeletable() {
		return !hasAnswer;
	}

	public void updateContent(String content) {
		if (hasAnswer) {
			throw new BusinessException(ErrorCode.QNA_400_ALREADY_ANSWERED);
		}
		this.content = content;
	}

	public void validateDeletableOrThrow() {
		if (hasAnswer) {
			throw new BusinessException(ErrorCode.QNA_400_ALREADY_ANSWERED);
		}
	}

	public void attachAnswer(Answer answer) {
		if (answer == null) {
			throw new BusinessException(ErrorCode.QNA_400_ANSWER_REQUIRED);
		}
		if (this.answer != null || this.hasAnswer) {
			throw new BusinessException(ErrorCode.QNA_409_ANSWER_ALREADY_EXISTS);
		}
		this.answer = answer;
		this.hasAnswer = true;

		if (answer.getQuestion() != this) {
			answer.attachQuestion(this);
		}
	}
}
