package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false, unique = true, length = 20)
	private String nickname;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, unique = true, length = 20)
	private String username;

	@Column(nullable = true, length = 255)
	private String password;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberRole role;

	@OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialAccount> socialAccounts = new ArrayList<>();

	private MemberProfile(
		String name, String nickname, String email, String username, String password, MemberRole role
	) {
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public static MemberProfile create(String name, String nickname, String email, String username, String password) {
		return new MemberProfile(
			name == null ? "OAuth2 User" : name,
			nickname,
			email,
			username,
			password,
			MemberRole.STUDENT
		);
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void updateStatus(MemberStatus status) {
		this.status = status;
	}

	public void updateRole(MemberRole role) {
		this.role = role;
	}

	public boolean isLocalUser() {
		return socialAccounts.isEmpty();
	}

	public boolean isOAuth2User() {
		return !socialAccounts.isEmpty();
	}

	public boolean hasPassword() {
		return this.password != null && !this.password.isEmpty();
	}
}
