package kr.java.aibe4_project2_team2_be.majormate.domain.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@Column(nullable = false, unique = true)
	private Long interviewId;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	private Review(Long interviewId, int rating, String content) {
		this.interviewId = interviewId;
		this.rating = rating;
		this.content = content;
	}

	public static Review create(Long interviewId, int rating, String content) {
		return new Review(interviewId, rating, content);
	}

	public void update(int rating, String content) {
		this.rating = rating;
		this.content = content;
	}
}
