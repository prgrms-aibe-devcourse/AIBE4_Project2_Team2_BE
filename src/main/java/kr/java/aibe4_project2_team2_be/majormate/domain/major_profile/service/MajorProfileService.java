package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfileLike;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileLikeRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorProfileService {
	private final MajorProfileRepository majorProfileRepository;
	private final MajorProfileLikeRepository majorProfileLikeRepository;
	private final MemberInfoReader memberInfoReader;

	public Long createProfile(Long memberId, MajorProfileCreateRequest request) {
		MemberProfile memberProfile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		if (memberProfile.getRole() != MemberRole.MAJOR) {
			throw new BusinessExceptionNew(ErrorCodeNew.COMMON_403);
		}

		MajorProfile majorProfile = MajorProfile.createProfile(
			memberProfile,
			request.getTitle(),
			request.getContent(),
			request.getTags()
		);

		return majorProfileRepository.save(majorProfile).getMajorProfileId();
	}

	// 수정

	public void updateProfile(Long memberId, MajorProfileCreateRequest request) {
		MajorProfile profile = majorProfileRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MAJOR_404_PROFILE_REQUIRED));

		profile.updateProfile(
			request.getTitle(),
			request.getContent(),
			request.getTags()
		);
	}

	@Transactional(readOnly = true)
	public MajorProfileResponse getMyProfile(Long memberId) {
		MajorProfile profile = majorProfileRepository.findByMemberProfile_MemberId(memberId)
			.orElse(null);

		if (profile == null) {
			return null;
		}

		MemberAcademic academic = memberInfoReader.getAcademicOrNull(memberId);
		
		long likeCount = majorProfileLikeRepository.countByMajorProfile_MajorProfileId(profile.getMajorProfileId());
		boolean isLiked = majorProfileLikeRepository.existsByMajorProfile_MajorProfileIdAndMemberId(profile.getMajorProfileId(), memberId);

		return MajorProfileResponse.of(profile, academic, likeCount, isLiked);
	}

	public void toggleProfileActive(Long memberId) {
		MajorProfile profile = majorProfileRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MAJOR_404_PROFILE_REQUIRED));
		profile.toggleActive();
	}

	@Transactional(readOnly = true)
	public List<MajorCardResponse> getMajorCards() {
		// 1. Fetch Join 쿼리 호출 (N+1 발생 안 함)
		List<MajorProfile> profiles = majorProfileRepository.findAllActiveWithAcademic();

		// 2. 좋아요 개수 일괄 조회
		List<Long> ids = profiles.stream().map(MajorProfile::getMajorProfileId).toList();
		Map<Long, Long> likeCounts = majorProfileRepository.countLikesByProfileIds(ids)
			.stream()
			.collect(Collectors.toMap(row -> (Long)row[0], row -> (Long)row[1]));

		// 3. 현재 로그인한 사용자의 좋아요 여부 일괄 조회
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		Set<Long> likedProfileIds;
		if (currentMemberId != 0L && !ids.isEmpty()) {
			likedProfileIds = majorProfileLikeRepository.findAllByMemberIdAndMajorProfile_MajorProfileIdIn(currentMemberId, ids)
				.stream()
				.map(like -> like.getMajorProfile().getMajorProfileId())
				.collect(Collectors.toSet());
		} else {
			likedProfileIds = Collections.emptySet();
		}

		return profiles.stream()
			.map(profile -> {
				MemberAcademic academic = profile.getMemberProfile().getAcademic();
				Long likeCount = likeCounts.getOrDefault(profile.getMajorProfileId(), 0L);
				boolean isLiked = likedProfileIds.contains(profile.getMajorProfileId());

				return MajorCardResponse.of(profile, academic, likeCount, isLiked);
			})
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public MajorProfileResponse getMajorCardDetail(Long profileId) {
		MajorProfile profile = majorProfileRepository.findById(profileId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MAJOR_404_PROFILE_REQUIRED));

		if (!profile.isActive()) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_PROFILE_NOT_ACTIVE);
		}

		Long memberId = profile.getMemberProfile().getMemberId();
		MemberAcademic academic = memberInfoReader.getAcademicOrNull(memberId);

		Long currentMemberId = SecurityUtil.getCurrentMemberId();

		long likeCount = majorProfileLikeRepository.countByMajorProfile_MajorProfileId(profileId);
		boolean isLiked = currentMemberId != 0L && majorProfileLikeRepository.existsByMajorProfile_MajorProfileIdAndMemberId(profileId, currentMemberId);

		return MajorProfileResponse.of(profile, academic, likeCount, isLiked);
	}

	public void toggleLike(Long memberId, Long profileId) {
		MajorProfile profile = majorProfileRepository.findById(profileId)
			.orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));

		if (majorProfileLikeRepository.existsByMajorProfile_MajorProfileIdAndMemberId(profileId, memberId)) {
			majorProfileLikeRepository.deleteByMajorProfile_MajorProfileIdAndMemberId(profileId, memberId);
		} else {
			majorProfileLikeRepository.save(MajorProfileLike.createLike(profile, memberId));
		}
	}
}
