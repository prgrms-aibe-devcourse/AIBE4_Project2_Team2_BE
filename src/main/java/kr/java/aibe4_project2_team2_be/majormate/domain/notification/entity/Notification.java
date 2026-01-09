package kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(nullable = false)
    private Long receiverId; // 받는 사람

    private Long senderId;   // 보낸 사람

    @Column(nullable = false)
    private String notificationType; // 알림 타입

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 내용

    @Column(nullable = false)
    private String relatedUrl; // 이동할 URL

    @Column(nullable = false)
    private boolean isRead;  // 읽음 여부

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Builder
    public Notification(Long receiverId, Long senderId, String notificationType, String content, String relatedUrl) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.notificationType = notificationType;
        this.content = content;
        this.relatedUrl = relatedUrl;
        this.isRead = false;
    }
}