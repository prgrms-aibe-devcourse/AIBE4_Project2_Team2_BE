package kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	indexes = {
		@Index(columnList = "student_member_id,status"),
		@Index(columnList = "major_member_id,status")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interview extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long interviewId;

	@Column(nullable = false)
	private Long studentMemberId;

	@Column(nullable = false)
	private Long majorMemberId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private String interviewMethod;

	@Column(nullable = false)
	private LocalDateTime preferredDatetime;

	@Column(columnDefinition = "TEXT")
	private String extraDescription;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private InterviewStatus status;

	@Column(columnDefinition = "TEXT")
	private String majorMessage;

	@Builder
	public Interview(
		Long interviewId, Long studentMemberId, Long majorMemberId,
		String title, String content, String interviewMethod, LocalDateTime preferredDatetime, String extraDescription,
		InterviewStatus status, String majorMessage
	) {
		this.interviewId = interviewId;
		this.studentMemberId = studentMemberId;
		this.majorMemberId = majorMemberId;
		this.title = title;
		this.content = content;
		this.interviewMethod = interviewMethod;
		this.preferredDatetime = preferredDatetime;
		this.extraDescription = extraDescription;
		this.status = (status == null) ? InterviewStatus.PENDING : status;
		this.majorMessage = majorMessage;
	}
}
