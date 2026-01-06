package kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity.Notification;
import java.time.LocalDateTime;

// Java 17 record 기능 사용 (CONVENTION.md 규칙)
public record NotificationResponse(
        Long id,
        String type,
        String content,
        String url,
        boolean isRead,
        LocalDateTime createdAt
) {
    // Entity -> DTO 변환 메서드 (CONVENTION.md 규칙)
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType(),
                notification.getContent(),
                notification.getRelatedUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}