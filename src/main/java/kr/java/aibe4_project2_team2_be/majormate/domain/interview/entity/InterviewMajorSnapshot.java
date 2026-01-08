package kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewMajorSnapshot {

	@Id
	private Long interviewId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "interview_id", nullable = false)
	private Interview interview;

	@Column(columnDefinition = "TEXT")
	private String majorProfileImageUrl;

	@Column(nullable = false, length = 20)
	private String majorNickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberStatus majorStatus;

	@Column(nullable = false, length = 20)
	private String majorUniversity;

	@Column(nullable = false)
	private String majorMajor;

	@Builder
	public InterviewMajorSnapshot(
		Interview interview,
		String majorProfileImageUrl,
		String majorNickname,
		MemberStatus majorStatus,
		String majorUniversity,
		String majorMajor
	) {
		this.interview = interview;
		this.majorProfileImageUrl = majorProfileImageUrl;
		this.majorNickname = majorNickname;
		this.majorStatus = majorStatus;
		this.majorUniversity = majorUniversity;
		this.majorMajor = majorMajor;
	}
}
