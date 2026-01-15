package kr.java.aibe4_project2_team2_be.majormate.domain.notification.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 안 읽은 알림만 최신순으로 가져오기
    List<Notification> findAllByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);
}