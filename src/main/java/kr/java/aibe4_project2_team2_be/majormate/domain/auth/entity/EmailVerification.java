package kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity;

import jakarta.persistence.*;
import kr.java.aibe4_project2_team2_be.majormate.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(columnList = "email"))
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationType type;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean verified = false;

    @Builder
    public EmailVerification(String email, String code, VerificationType type, LocalDateTime expiresAt) {
        this.email = email;
        this.code = code;
        this.type = type;
        this.expiresAt = expiresAt;
        this.verified = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void verify() {
        this.verified = true;
    }

    public enum VerificationType {
        SIGNUP,           // 회원가입
        FIND_USERNAME,    // 아이디 찾기
        RESET_PASSWORD    // 비밀번호 재설정
    }
}
