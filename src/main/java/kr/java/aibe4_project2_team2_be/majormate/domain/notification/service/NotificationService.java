package kr.java.aibe4_project2_team2_be.majormate.domain.notification.service;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event.NotificationEvent;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity.Notification;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(60L * 1000 * 60); // 1시간 타임아웃

        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError((e) -> emitters.remove(memberId));

        // 503 에러 방지용 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(memberId);
        }
        return emitter;
    }

    @Transactional
    public void send(Long receiverId, Long senderId, String type, String content, String url) {

        Notification notification = notificationRepository.save(Notification.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .notificationType(type)
                .content(content)
                .relatedUrl(url)
                .build());

        SseEmitter emitter = emitters.get(receiverId);

        if (emitter != null) {
            try {
                // map으로 변경, string > JSON으로 내보냄
                Map<String, String> eventData = new HashMap<>();
                eventData.put("type", type);
                eventData.put("content", content);
                eventData.put("url", url);
                eventData.put("id", String.valueOf(notification.getId()));

                emitter.send(SseEmitter.event()
                        .name("notification")
                        .id(String.valueOf(notification.getId()))
                        .data(eventData));

                log.info("SSE 알림 전송 성공 [ReceiverId: {}]", receiverId);

            } catch (IOException e) {
                log.error("SSE 전송 실패. Emitter 삭제: {}", receiverId);
                emitters.remove(receiverId);
            }
        }
    }

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        this.send(
                event.receiverId(),
                event.senderId(),
                event.type(),
                event.content(),
                event.url()
        );
        log.info("이벤트 수신 및 알림 발송 완료: {}", event.content());
    }
}