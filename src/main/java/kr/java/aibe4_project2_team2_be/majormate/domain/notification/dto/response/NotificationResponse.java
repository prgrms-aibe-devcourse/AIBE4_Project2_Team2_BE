package kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity.Notification;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String content,
        String url,
        boolean isRead,
        LocalDateTime createdAt
) {
    // Entity -> DTO
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