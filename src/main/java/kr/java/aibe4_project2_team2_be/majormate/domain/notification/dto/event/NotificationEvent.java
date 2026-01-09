package kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event;

public record NotificationEvent(
        Long receiverId,   // 알림 받는 사람
        Long senderId,     // 알림 보낸 사람 (없으면 null)
        String type,       // 알림 타입 (COMMENT, LIKE 등)
        String content,    // 알림 내용
        String url         // 클릭 시 이동할 주소
) {
}