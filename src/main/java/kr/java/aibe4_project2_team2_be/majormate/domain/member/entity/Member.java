package kr.java.aibe4_project2_team2_be.majormate.domain.member.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberType;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "member_type")
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "member_status")
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Builder
    public Member(String username, String email, String password, String name, String nickname,
                  MemberType memberType, MemberStatus memberStatus, MemberRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.memberType = memberType;
        this.memberStatus = memberStatus;
        this.role = role;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public void updateRole(MemberRole role) {
        this.role = role;
    }

    public void updateMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    public boolean isStudent() {
        return this.memberType == MemberType.STUDENT;
    }

    public boolean isMajor() {
        return this.memberType == MemberType.MAJOR;
    }
}
