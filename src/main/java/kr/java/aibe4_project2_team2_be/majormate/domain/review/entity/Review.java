package kr.java.aibe4_project2_team2_be.majormate.domain.review.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reviewId;

	@Column(nullable = false, unique = true)
	private Long interviewId;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public Review(Long interviewId, int rating, String content) {
		this.interviewId = interviewId;
		this.rating = rating;
		this.content = content;
	}

	@PrePersist
	void prePersist() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}
