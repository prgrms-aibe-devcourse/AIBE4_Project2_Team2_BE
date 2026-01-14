// 경로: src/main/java/kr/java/aibe4_project2_team2_be/majormate/domain/admin/scheduler/DocumentCleanupScheduler.java

package kr.java.aibe4_project2_team2_be.majormate.domain.admin.scheduler;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminMajorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentCleanupScheduler {

    private final AdminMajorService adminMajorService;

    // 매일 자정 (00:00:00) 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldDocuments() {
        log.info("[Scheduler] 오래된 증빙 서류 삭제 작업 시작");
        adminMajorService.deleteExpiredDocuments();
        log.info("[Scheduler] 오래된 증빙 서류 삭제 작업 종료");
    }
}