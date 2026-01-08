package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "major_profile_tags")
@Getter
@NoArgsConstructor
public class MajorProfileTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tagId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_profile_id", nullable = false)
	private MajorProfile majorProfile;

	@Column(name = "tag_name", nullable = false, length = 50)
	private String tagName;

	public static MajorProfileTag createTag(MajorProfile majorProfile, String tagName) {
		MajorProfileTag tag = new MajorProfileTag();
		tag.majorProfile = majorProfile;
		tag.tagName = tagName;
		return tag;
	}
}
