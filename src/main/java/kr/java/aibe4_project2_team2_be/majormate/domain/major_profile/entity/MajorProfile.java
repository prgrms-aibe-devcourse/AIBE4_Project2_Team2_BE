package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.ApplicationStatus;
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
	private Member member;

	@Column(name = "title", nullable = false, length = 100)
	private String title;

	@Column(name = "content", nullable = false, length = 512)
	private String content;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	// 태그
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(
		name = "major_profile_tags",
		joinColumns = @JoinColumn(name = "major_profile_id")
	)

	@Column(name = "tag_name")
	private List<String> tags = new ArrayList<>();

	// 생성
	public static MajorProfile createProfile(Member member, String title, String content, List<String> tags) {
		MajorProfile profile = new MajorProfile();
		profile.member = member;
		profile.title = title;
		profile.content = content;
		profile.isActive = true;
		profile.tags = tags;
		return profile;
	}

	// 수정
	public void updateProfile(String title, String content, List<String> tags) {
		this.title = title;
		this.content = content;
		this.tags = tags;
		this.tags.clear();
		if(tags != null) this.tags.addAll(tags);
	}
}
