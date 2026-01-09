package kr.java.aibe4_project2_team2_be.majormate.domain.admin.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {
}
