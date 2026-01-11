package kr.java.aibe4_project2_team2_be.majormate.domain.interview.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "interview_major_snapshot")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewMajorSnapshot {

	@Id
	@Column(name = "interview_id", nullable = false)
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
		String profileImageUrl,
		String nickname,
		MemberStatus status,
		String university,
		String major
	) {
		this.interviewForm = Objects.requireNonNull(interviewForm, "interviewForm must not be null");
		this.profileImageUrl = profileImageUrl;
		this.nickname = requireText(nickname, "nickname");
		this.status = Objects.requireNonNull(status, "status must not be null");
		this.university = requireText(university, "university");
		this.major = requireText(major, "major");
	}

	public static InterviewMajorSnapshot create(
		InterviewForm form, MemberProfile majorProfile, MemberAcademic academic
	) {
		Objects.requireNonNull(form, "form must not be null");
		Objects.requireNonNull(majorProfile, "majorProfile must not be null");
		Objects.requireNonNull(academic, "academic must not be null");

		if (majorProfile.getStatus() == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_STATUS_REQUIRED);
		}
		if (isBlank(academic.getUniversity()) || isBlank(academic.getMajor())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_ACADEMIC_REQUIRED);
		}

		return new InterviewMajorSnapshot(
			form,
			majorProfile.getProfileImageUrl(),
			majorProfile.getNickname(),
			majorProfile.getStatus(),
			academic.getUniversity(),
			academic.getMajor()
		);
	}

	private static String requireText(String value, String fieldName) {
		if (isBlank(value)) {
			throw new BusinessExceptionNew(ErrorCodeNew.COMMON_400);
		}
		return value;
	}

	private static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
