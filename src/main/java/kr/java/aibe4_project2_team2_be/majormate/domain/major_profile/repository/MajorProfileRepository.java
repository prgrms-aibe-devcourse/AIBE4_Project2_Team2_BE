package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;

@Repository
public interface MajorProfileRepository extends JpaRepository<MajorProfile, Long> {

	Optional<MajorProfile> findByMemberProfile_MemberId(Long memberId);
}
