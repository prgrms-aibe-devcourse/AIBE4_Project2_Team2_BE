package kr.java.aibe4_project2_team2_be.majormate.domain.notification.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.event.NotificationEvent;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그용
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Object principal) {

        log.info(">>>> [SSE 디버그] Principal Class Type: {}", principal.getClass().getName());

        Long memberId = null;

        if (principal instanceof Long) {
            memberId = (Long) principal;
        }
        else if (principal instanceof UserDetails) {
            UserDetails user = (UserDetails) principal;
            memberId = Long.parseLong(user.getUsername());
        }
        // String type 처리로직
        else if (principal instanceof String) {
            String principalStr = (String) principal;
            log.info(">>>> [SSE 정보] 타입이 String입니다. 값: {}", principalStr);
            try {
                memberId = Long.parseLong(principalStr);
            } catch (NumberFormatException e) {
                log.error(">>>> [SSE 에러] String 타입이지만 숫자로 변환할 수 없습니다: {}", principalStr);
            }
        }

        if (memberId == null) {
            throw new IllegalArgumentException("ID 추출 실패: " + principal.getClass().getName());
        }

        return notificationService.subscribe(memberId);
    }
    @PostMapping("/send-test")
    public String sendTest(@RequestParam Long receiverId, @RequestParam String content) {
        eventPublisher.publishEvent(new NotificationEvent(
                receiverId,
                null,
                "TEST_EVENT",
                content,
                "/test-url"
        ));
        return "이벤트 발행 성공!";
    }

}