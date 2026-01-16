package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.SocialAccount;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.AuthProvider;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_profile")
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

	@Column(length = 255)
	private String password;

	@Column(columnDefinition = "TEXT")
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberStatus status = MemberStatus.ETC;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberRole role = MemberRole.STUDENT;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AuthProvider authProvider;

	@OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<SocialAccount> socialAccounts = new ArrayList<>();

	@OneToOne(mappedBy = "memberProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private MemberAcademic academic;

	private MemberProfile(
		String name, String nickname, String email, String username, String password, MemberStatus status,
		MemberRole role, AuthProvider authProvider
	) {
		this.name = requireText(name, "name");
		this.nickname = requireText(nickname, "nickname");
		this.email = requireText(email, "email");
		this.username = requireText(username, "username");
		this.password = password;
		this.status = Objects.requireNonNull(status, "status must not be null");
		this.role = Objects.requireNonNull(role, "role must not be null");
		this.authProvider = Objects.requireNonNull(authProvider, "authProvider must not be null");
	}

	public static MemberProfile createLocal(
		String name, String nickname, String email, String username, String encodedPassword, MemberStatus status
	) {
		if (encodedPassword == null || encodedPassword.isBlank()) {
			throw new BusinessException(ErrorCode.MEMBER_400_PASSWORD_REQUIRED);
		}
		return new MemberProfile(
			name, nickname, email, username, encodedPassword, status, MemberRole.STUDENT, AuthProvider.LOCAL
		);
	}

	public static MemberProfile createOAuth2(
		String name, String nickname, String email, String username, AuthProvider provider
	) {
		Objects.requireNonNull(provider, "provider must not be null");
		if (provider == AuthProvider.LOCAL) {
			throw new BusinessException(ErrorCode.MEMBER_400_INVALID_AUTH_PROVIDER);
		}
		return new MemberProfile(
			name, nickname, email, username, null, MemberStatus.ETC, MemberRole.STUDENT, provider
		);
	}

	public boolean isLocalUser() {
		return this.authProvider == AuthProvider.LOCAL;
	}

	public boolean isOAuth2User() {
		return this.authProvider != AuthProvider.LOCAL;
	}

	public boolean hasPassword() {
		return this.password != null && !this.password.isBlank();
	}

	public void attachAcademic(MemberAcademic academic) {
		this.academic = academic;
		if (academic != null && academic.getMemberProfile() != this) {
			academic.attachMemberProfile(this);
		}
	}

	public void updateNickname(String nickname) {
		this.nickname = requireText(nickname, "nickname");
	}

	public void updatePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void updateStatus(MemberStatus status) {
		this.status = status;
	}

	public void grantMajorRole() {
		assertRoleMutable();
        if (this.role == MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.MEMBER_400_INVALID_ROLE_TRANSITION);
        }
		this.role = MemberRole.MAJOR;
	}

    public void grantAdminRole() {
        this.role = MemberRole.ADMIN;
    }
	public void revokeMajorRole() {
		assertRoleMutable();
		if (this.role == MemberRole.STUDENT) {
			throw new BusinessException(ErrorCode.MEMBER_400_INVALID_ROLE_TRANSITION);
		}
		this.role = MemberRole.STUDENT;
	}

	private void assertRoleMutable() {
		if (this.role == MemberRole.ADMIN) {
			throw new BusinessException(ErrorCode.MEMBER_400_ROLE_CHANGE_NOT_ALLOWED);
		}
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " must not be blank");
		}
		return value;
	}
}
