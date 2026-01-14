package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;

@Repository
public interface MajorProfileRepository extends JpaRepository<MajorProfile, Long> {

	Optional<MajorProfile> findByMemberProfile_MemberId(Long memberId);

	// @Query(value = "SELECT p FROM MajorProfile p " +
	// 	"JOIN FETCH p.memberProfile mp " +
	// 	"JOIN FETCH mp.academic ma " +
	// 	"LEFT JOIN MajorProfileLike l ON p.majorProfileId = l.majorProfile.majorProfileId " +
	// 	"LEFT JOIN MajorProfileLike myLike ON p.majorProfileId = myLike.majorProfile.majorProfileId AND myLike.memberId = :memberId " +
	// 	"WHERE p.isActive = true " +
	// 	"GROUP BY p.majorProfileId " +
	// 	"ORDER BY COUNT(l) DESC, " +
	// 	"CASE WHEN COUNT(myLike) > 0 THEN 1 ELSE 0 END DESC, " +
	// 	"p.createdAt DESC",
	// 	countQuery = "SELECT count(p) FROM MajorProfile p WHERE p.isActive = true")
	// Page<MajorProfile> findAllActiveWithAcademic(Pageable pageable, @Param("memberId") Long memberId);

	@Query(value = "SELECT DISTINCT p FROM MajorProfile p " +
		"JOIN FETCH p.memberProfile mp " +
		"JOIN FETCH mp.academic ma " +
		"LEFT JOIN p.tags t " +
		"WHERE p.isActive = true " +
		"AND (:keyword IS NULL OR :keyword = '' OR " +
		"    (:searchType = 'tag' AND t.tagName = :keyword) OR " +
		"    (:searchType <> 'tag' AND (ma.university LIKE %:keyword% OR ma.major LIKE %:keyword% OR mp.nickname LIKE %:keyword% OR p.title LIKE %:keyword%))"
		+
		")",
		countQuery = "SELECT count(DISTINCT p) FROM MajorProfile p " +
			"JOIN p.memberProfile mp " +
			"JOIN mp.academic ma " +
			"LEFT JOIN p.tags t " +
			"WHERE p.isActive = true " +
			"AND (:keyword IS NULL OR :keyword = '' OR " +
			"    (:searchType = 'tag' AND t.tagName = :keyword) OR " +
			"    (:searchType <> 'tag' AND (ma.university LIKE %:keyword% OR ma.major LIKE %:keyword% OR mp.nickname LIKE %:keyword% OR p.title LIKE %:keyword%))"
			+
			")")
	Page<MajorProfile> searchActiveWithAcademic(
		@Param("searchType") String searchType,
		@Param("keyword") String keyword,
		Pageable pageable
	);

	@Query("SELECT l.majorProfile.majorProfileId, COUNT(l) " +
		"FROM MajorProfileLike l " +
		"WHERE l.majorProfile.majorProfileId IN :ids " +
		"GROUP BY l.majorProfile.majorProfileId")
	List<Object[]> countLikesByProfileIds(@Param("ids") List<Long> ids);
}
