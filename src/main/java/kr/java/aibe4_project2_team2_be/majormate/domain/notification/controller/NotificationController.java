package kr.java.aibe4_project2_team2_be.majormate.domain.notification.controller;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.dto.response.NotificationResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.notification.service.NotificationService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    @GetMapping("/unread")
    public ApiResponseNew<List<NotificationResponse>> getUnreadNotifications(@AuthenticationPrincipal Object principal) {
        Long memberId = getMemberId(principal);

        List<NotificationResponse> responses = notificationService.getUnreadNotifications(memberId);
        return ApiResponseNew.success(responses);
    }

    // 3. 알림 읽음 처리
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
        return ResponseEntity.ok(ApiResponseNew.success(null));
    }

    private Long getMemberId(Object principal) {
        if (principal == null) {
            log.error(">>>> [인증 에러] Principal 객체가 NULL입니다.");
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다.");
        }

        log.info(">>>> [디버그] Principal Type: {}", principal.getClass().getName());

        try {
            // Case 1: UserDetails 타입 (일반적인 Spring Security)
            if (principal instanceof UserDetails) {
                return Long.parseLong(((UserDetails) principal).getUsername());
            }
            // Case 2: Long 타입 (Custom Filter에서 ID를 직접 넣은 경우)
            else if (principal instanceof Long) {
                return (Long) principal;
            }
            // Case 3: String 타입 (Custom Filter에서 String ID를 넣은 경우)
            else if (principal instanceof String) {
                return Long.parseLong((String) principal);
            }
            // Case 4: 그 외 (커스텀 UserDetails 등) - toString으로 시도
            else {
                log.warn(">>>> [주의] 예상치 못한 Principal 타입: {}", principal.getClass());
                return Long.parseLong(principal.toString());
            }
        } catch (NumberFormatException e) {
            log.error(">>>> [변환 에러] ID를 Long으로 변환할 수 없습니다. 값: {}", principal);
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
    }

}
