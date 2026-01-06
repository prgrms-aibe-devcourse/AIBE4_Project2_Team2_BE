package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAcademic extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long academicId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false, unique = true)
	private Member member;

	@Column(nullable = false, length = 20)
	private String university;

	@Column(nullable = false)
	private String major;

	@Builder
	public MemberAcademic(Long academicId, Member member, String university, String major) {
		this.academicId = academicId;
		this.member = member;
		this.university = university;
		this.major = major;
	}

	public void updateUniversity(String university) {
		this.university = university;
	}

	public void updateMajor(String major) {
		this.major = major;
	}
}
