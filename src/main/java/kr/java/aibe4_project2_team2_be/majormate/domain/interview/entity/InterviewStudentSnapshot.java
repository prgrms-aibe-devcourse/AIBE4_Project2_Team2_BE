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
public class InterviewStudentSnapshot {

	@Id
	private Long interviewId;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "interview_id", nullable = false)
	private Interview interview;

	@Column(columnDefinition = "TEXT")
	private String studentProfileImageUrl;

	@Column(nullable = false, length = 20)
	private String studentNickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberStatus studentStatus;

	@Column(nullable = false, length = 20)
	private String studentUniversity;

	@Column(nullable = false)
	private String studentMajor;

	@Builder
	public InterviewStudentSnapshot(
		Interview interview,
		String studentProfileImageUrl,
		String studentNickname,
		MemberStatus studentStatus,
		String studentUniversity,
		String studentMajor
	) {
		this.interview = interview;
		this.studentProfileImageUrl = studentProfileImageUrl;
		this.studentNickname = studentNickname;
		this.studentStatus = studentStatus;
		this.studentUniversity = studentUniversity;
		this.studentMajor = studentMajor;
	}
}
