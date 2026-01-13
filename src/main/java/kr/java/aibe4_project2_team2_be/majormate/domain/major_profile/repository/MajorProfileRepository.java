package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;

@Repository
public interface MajorProfileRepository extends JpaRepository<MajorProfile, Long> {

	Optional<MajorProfile> findByMemberProfile_MemberId(Long memberId);

	@Query("SELECT p FROM MajorProfile p " +
		"JOIN FETCH p.memberProfile mp " +
		"JOIN FETCH mp.academic ma " +
		"WHERE p.isActive = true")
	List<MajorProfile> findAllActiveWithAcademic();

	@Query("SELECT l.majorProfile.majorProfileId, COUNT(l) " +
		"FROM MajorProfileLike l " +
		"WHERE l.majorProfile.majorProfileId IN :ids " +
		"GROUP BY l.majorProfile.majorProfileId")
	List<Object[]> countLikesByProfileIds(@Param("ids") List<Long> ids);
}
