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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "interview_student_snapshot")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewStudentSnapshot {

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
	@Column(length = 20)
	private MemberStatus status;

	@Column(length = 20)
	private String university;

	@Column(length = 20)
	private String major;

	private InterviewStudentSnapshot(
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
		this.status = status;
		this.university = university;
		this.major = major;
	}

	public static InterviewStudentSnapshot create(InterviewForm form, MemberProfile student, MemberAcademic academic) {
		Objects.requireNonNull(form, "form must not be null");
		Objects.requireNonNull(student, "student must not be null");

		String university = academic != null ? academic.getUniversity() : null;
		String major = academic != null ? academic.getMajor() : null;

		return new InterviewStudentSnapshot(
			form,
			student.getProfileImageUrl(),
			student.getNickname(),
			student.getStatus(),
			university,
			major
		);
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " must not be blank");
		}
		return value;
	}
}
