package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(nullable = false, unique = true, length = 20)
	private String nickname;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true, length = 20)
	private String username;

	@Column(nullable = true, length = 255)
	private String password;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberRole role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialAccount> socialAccounts = new ArrayList<>();

	@Builder
	public Member(
		Long memberId,
		String name, String nickname, String email, String username, String password, String profileImageUrl,
		MemberStatus status, MemberRole role
	) {
		this.memberId = memberId;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.username = username;
		this.password = password;
		this.profileImageUrl = profileImageUrl;
		this.status = status;
		this.role = role;
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

	public boolean isOAuth2User() {
		return !socialAccounts.isEmpty();
	}

	public boolean isLocalUser() {
		return socialAccounts.isEmpty();
	}

	public boolean hasPassword() {
		return this.password != null && !this.password.isEmpty();
	}
}
