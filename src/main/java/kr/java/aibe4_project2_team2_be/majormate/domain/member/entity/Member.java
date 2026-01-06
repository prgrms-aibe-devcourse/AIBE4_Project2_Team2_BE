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
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = true, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "member_status")
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

    @Builder
    public Member(String username, String email, String password, String name, String nickname,
                  MemberStatus memberStatus, MemberRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.memberStatus = memberStatus;
        this.role = role;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

    public void updateMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
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
