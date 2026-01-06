package kr.java.aibe4_project2_team2_be.majormate.domain.notification.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.context.ApplicationEventPublisher;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event.NotificationEvent;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long memberId) {
        return notificationService.subscribe(memberId);
    }

    //테스트용 알림발송 API
    @PostMapping("/send-test")
    public String sendTest(@RequestParam Long receiverId, @RequestParam String content) {

        eventPublisher.publishEvent(new NotificationEvent(
                receiverId,
                null,
                "TEST_EVENT",
                content,
                "/test-url"
        ));

        return "이벤트 발행 성공! (알림이 갔는지 확인하세요)";
    }
}