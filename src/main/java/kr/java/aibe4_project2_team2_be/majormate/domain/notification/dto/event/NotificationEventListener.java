package kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    // Rollback 되면 알림도 취소
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(NotificationEvent event) {
        log.info("알림 이벤트 수신: {}", event.content());

        notificationService.send(
                event.receiverId(),
                event.senderId(),
                event.type(),
                event.content(),
                event.url()
        );
    }
}