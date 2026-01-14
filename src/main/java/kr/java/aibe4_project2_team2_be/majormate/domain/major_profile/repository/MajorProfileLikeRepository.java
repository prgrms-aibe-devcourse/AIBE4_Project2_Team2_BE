package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfileLike;

@Repository
public interface MajorProfileLikeRepository extends JpaRepository<MajorProfileLike, Integer> {

	boolean existsByMajorProfile_MajorProfileIdAndMemberId(Long majorProfileId, Long memberId);

	Optional<MajorProfileLike> findByMajorProfile_MajorProfileIdAndMemberId(Long profileId, Long memberId);
	long countByMajorProfile_MajorProfileId(Long profileId);

	List<MajorProfileLike> findAllByMemberIdAndMajorProfile_MajorProfileIdIn(Long memberId, List<Long> majorProfileIds);
}
