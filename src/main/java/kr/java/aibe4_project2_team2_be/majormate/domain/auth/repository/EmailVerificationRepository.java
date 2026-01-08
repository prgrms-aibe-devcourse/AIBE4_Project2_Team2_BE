package kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailAndTypeOrderByCreatedAtDesc(String email, VerificationType type);

    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.email = :email AND e.type = :type")
    void deleteByEmailAndType(@Param("email") String email, @Param("type") VerificationType type);
}
