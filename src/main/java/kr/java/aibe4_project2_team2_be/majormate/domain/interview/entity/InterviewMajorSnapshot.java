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
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import lombok.AccessLevel;
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
	private InterviewForm interviewForm;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Column(nullable = false, length = 20)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberStatus status;

	@Column(nullable = false, length = 20)
	private String university;

	@Column(nullable = false, length = 20)
	private String major;

	private InterviewMajorSnapshot(
		InterviewForm interviewForm,
		String profileImageUrl, String nickname, MemberStatus status, String university, String major
	) {
		this.interviewForm = interviewForm;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.status = status;
		this.university = university;
		this.major = major;
	}

	public static InterviewMajorSnapshot create(InterviewForm form, MemberProfile major, MemberAcademic academic) {
		return new InterviewMajorSnapshot(
			form,
			major.getProfileImageUrl(),
			major.getNickname(),
			major.getStatus(),
			academic.getUniversity(),
			academic.getMajor()
		);
	}
}
