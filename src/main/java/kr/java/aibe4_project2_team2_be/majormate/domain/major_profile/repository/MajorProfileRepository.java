package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Query(value = "SELECT p FROM MajorProfile p " +
		"JOIN FETCH p.memberProfile mp " +
		"JOIN FETCH mp.academic ma " +
		"LEFT JOIN MajorProfileLike l ON p.majorProfileId = l.majorProfile.majorProfileId " +
		"LEFT JOIN MajorProfileLike myLike ON p.majorProfileId = myLike.majorProfile.majorProfileId AND myLike.memberId = :memberId " +
		"WHERE p.isActive = true " +
		"GROUP BY p.majorProfileId " +
		"ORDER BY COUNT(l) DESC, " +
		"CASE WHEN COUNT(myLike) > 0 THEN 1 ELSE 0 END DESC, " +
		"p.createdAt DESC",
		countQuery = "SELECT count(p) FROM MajorProfile p WHERE p.isActive = true")
	Page<MajorProfile> findAllActiveWithAcademic(Pageable pageable, @Param("memberId") Long memberId);

	@Query("SELECT l.majorProfile.majorProfileId, COUNT(l) " +
		"FROM MajorProfileLike l " +
		"WHERE l.majorProfile.majorProfileId IN :ids " +
		"GROUP BY l.majorProfile.majorProfileId")
	List<Object[]> countLikesByProfileIds(@Param("ids") List<Long> ids);

	// 통합 검색 (닉네임, 학교, 학과)
	@Query(value = "SELECT p FROM MajorProfile p " +
		"JOIN FETCH p.memberProfile mp " +
		"JOIN FETCH mp.academic ma " +
		"LEFT JOIN MajorProfileLike l ON p.majorProfileId = l.majorProfile.majorProfileId " +
		"LEFT JOIN MajorProfileLike myLike ON p.majorProfileId = myLike.majorProfile.majorProfileId AND myLike.memberId = :memberId " +
		"WHERE p.isActive = true " +
		"AND (LOWER(mp.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(ma.university) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(ma.major) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
		"GROUP BY p.majorProfileId " +
		"ORDER BY COUNT(l) DESC, " +
		"CASE WHEN COUNT(myLike) > 0 THEN 1 ELSE 0 END DESC, " +
		"p.createdAt DESC",
		countQuery = "SELECT count(DISTINCT p) FROM MajorProfile p " +
			"JOIN p.memberProfile mp " +
			"JOIN mp.academic ma " +
			"WHERE p.isActive = true " +
			"AND (LOWER(mp.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(ma.university) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(ma.major) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<MajorProfile> findAllActiveWithAcademicByAllSearch(
		Pageable pageable,
		@Param("memberId") Long memberId,
		@Param("keyword") String keyword
	);

	// 태그 검색
	@Query(value = "SELECT p FROM MajorProfile p " +
		"JOIN FETCH p.memberProfile mp " +
		"JOIN FETCH mp.academic ma " +
		"JOIN p.tags t " +
		"LEFT JOIN MajorProfileLike l ON p.majorProfileId = l.majorProfile.majorProfileId " +
		"LEFT JOIN MajorProfileLike myLike ON p.majorProfileId = myLike.majorProfile.majorProfileId AND myLike.memberId = :memberId " +
		"WHERE p.isActive = true " +
		"AND LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"GROUP BY p.majorProfileId " +
		"ORDER BY COUNT(l) DESC, " +
		"CASE WHEN COUNT(myLike) > 0 THEN 1 ELSE 0 END DESC, " +
		"p.createdAt DESC",
		countQuery = "SELECT count(DISTINCT p) FROM MajorProfile p " +
			"JOIN p.tags t " +
			"WHERE p.isActive = true " +
			"AND LOWER(t.tagName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<MajorProfile> findAllActiveWithAcademicByTag(
		Pageable pageable,
		@Param("memberId") Long memberId,
		@Param("keyword") String keyword
	);
}
