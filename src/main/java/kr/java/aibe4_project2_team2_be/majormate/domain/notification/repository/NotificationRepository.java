package kr.java.aibe4_project2_team2_be.majormate.domain.notification.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 회원의 알림을 최신순으로 가져오기
    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}