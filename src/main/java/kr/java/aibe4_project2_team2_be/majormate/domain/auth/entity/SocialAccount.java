package kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.AuthProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_account",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_provider_provider_id",
            columnNames = {"auth_provider", "provider_id"}
        )
    },
    indexes = {
        @Index(name = "idx_member_id", columnList = "member_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 20)
    private AuthProvider authProvider;

    @Column(name = "provider_id", nullable = false, length = 100)
    private String providerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public SocialAccount(Member member, AuthProvider authProvider, String providerId) {
        this.member = member;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.createdAt = LocalDateTime.now();
    }
}
