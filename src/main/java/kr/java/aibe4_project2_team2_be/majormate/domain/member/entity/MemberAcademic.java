package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import java.util.Objects;

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
@Getter
@Table(name = "member_academic")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAcademic extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long academicId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false, unique = true)
	private MemberProfile memberProfile;

	@Column(length = 20)
	private String university;

	@Column(length = 20)
	private String major;

	private MemberAcademic(MemberProfile memberProfile) {
		this.memberProfile = Objects.requireNonNull(memberProfile, "memberProfile must not be null");
	}

	public static MemberAcademic create(MemberProfile profile) {
		return new MemberAcademic(profile);
	}

	void attachMemberProfile(MemberProfile profile) {
		this.memberProfile = profile;
	}

	public void updateUniversity(String university) {
		this.university = university;
	}

	public void updateMajor(String major) {
		this.major = major;
	}
}
