package kr.java.aibe4_project2_team2_be.majormate.domain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.request.entity.MajorRoleRequest;

@Repository
public interface MajorRoleRequestRepository extends JpaRepository<MajorRoleRequest, Long> {

}
