package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MajorProfileLike {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_profile_id")
	private MajorProfile majorProfile;

	@Column(name = "member_id")
	private Long memberId;

	public static MajorProfileLike createLike(MajorProfile majorProfile, Long memberId) {
		MajorProfileLike like = new MajorProfileLike();
		like.majorProfile = majorProfile;
		like.memberId = memberId;
		return like;
	}
}
