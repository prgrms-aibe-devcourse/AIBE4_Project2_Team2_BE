package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "major_profile")
@Getter
@NoArgsConstructor
public class MajorProfile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long majorProfileId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false, unique = true)
	private MemberProfile memberProfile;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "content", nullable = false, length = 512)
	private String content;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	// 태그 (Entity로 변경)
	@OneToMany(mappedBy = "majorProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MajorProfileTag> tags = new ArrayList<>();

	// 생성
	public static MajorProfile createProfile(MemberProfile memberProfile, String title, String content, List<String> tagNames) {
		MajorProfile profile = new MajorProfile();
		profile.memberProfile = memberProfile;
		profile.title = title;
		profile.content = content;
		profile.isActive = true;
		
		if (tagNames != null) {
			for (String tagName : tagNames) {
				profile.tags.add(MajorProfileTag.createTag(profile, tagName));
			}
		}
		return profile;
	}

	// 수정
	public void updateProfile(String title, String content, List<String> tagNames) {
		this.title = title;
		this.content = content;
		
		// 기존 태그 삭제 및 새 태그 추가
		this.tags.clear();
		if (tagNames != null) {
			for (String tagName : tagNames) {
				this.tags.add(MajorProfileTag.createTag(this, tagName));
			}
		}
	}

	// 활성/비활성화
	public void toggleActive() {
		this.isActive = !this.isActive;
	}
}
