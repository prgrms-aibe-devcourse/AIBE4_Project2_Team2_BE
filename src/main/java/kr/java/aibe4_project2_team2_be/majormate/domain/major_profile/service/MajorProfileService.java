package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.LikeToggleResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfileLike;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileLikeRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberInfoReader;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
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
			throw new BusinessException(ErrorCode.COMMON_403);
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
			.orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_404_PROFILE_REQUIRED));

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
		boolean isLiked = majorProfileLikeRepository.existsByMajorProfile_MajorProfileIdAndMemberId(
			profile.getMajorProfileId(), memberId);

		return MajorProfileResponse.of(profile, academic, likeCount, isLiked);
	}

	public void toggleProfileActive(Long memberId) {
		MajorProfile profile = majorProfileRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_404_PROFILE_REQUIRED));
		profile.toggleActive();
	}

	@Transactional(readOnly = true)
	public Page<MajorCardResponse> getMajorCards(String searchType, String keyword, Pageable pageable) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		Page<MajorProfile> profiles;

		// 1. 검색 조건에 따른 쿼리 분기 (모두 정렬 로직 포함)
		if (keyword == null || keyword.trim().isEmpty()) {
			profiles = majorProfileRepository.findAllActiveWithAcademic(pageable, currentMemberId);
		} else if ("tag".equalsIgnoreCase(searchType)) {
			profiles = majorProfileRepository.findAllActiveWithAcademicByTag(pageable, currentMemberId, keyword);
		} else {
			profiles = majorProfileRepository.findAllActiveWithAcademicByAllSearch(pageable, currentMemberId, keyword);
		}

		// 검색 결과가 없는 경우 빈 페이지 즉시 반환
		if (profiles.isEmpty()) {
			return profiles.map(profile -> null);
		}

		// 2. 좋아요 개수 일괄 조회
		List<Long> ids = profiles.stream().map(MajorProfile::getMajorProfileId).toList();
		Map<Long, Long> likeCounts = majorProfileRepository.countLikesByProfileIds(ids)
			.stream()
			.collect(Collectors.toMap(row -> (Long)row[0], row -> (Long)row[1]));

		// 3. 현재 로그인한 사용자의 좋아요 여부 일괄 조회
		Set<Long> likedProfileIds;
		if (currentMemberId != 0L && !ids.isEmpty()) {
			likedProfileIds = majorProfileLikeRepository.findAllByMemberIdAndMajorProfile_MajorProfileIdIn(
					currentMemberId, ids)
				.stream()
				.map(like -> like.getMajorProfile().getMajorProfileId())
				.collect(Collectors.toSet());
		} else {
			likedProfileIds = Collections.emptySet();
		}

		return profiles.map(profile -> {
			MemberAcademic academic = profile.getMemberProfile().getAcademic();
			Long likeCount = likeCounts.getOrDefault(profile.getMajorProfileId(), 0L);
			boolean isLiked = likedProfileIds.contains(profile.getMajorProfileId());

			return MajorCardResponse.of(profile, academic, likeCount, isLiked);
		});
	}

	@Transactional(readOnly = true)
	public MajorProfileResponse getMajorCardDetail(Long profileId) {
		MajorProfile profile = majorProfileRepository.findById(profileId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_404_PROFILE_REQUIRED));

		if (!profile.isActive()) {
			throw new BusinessException(ErrorCode.MAJOR_400_PROFILE_NOT_ACTIVE);
		}

		Long memberId = profile.getMemberProfile().getMemberId();
		MemberAcademic academic = memberInfoReader.getAcademicOrNull(memberId);

		Long currentMemberId = SecurityUtil.getCurrentMemberId();

		long likeCount = majorProfileLikeRepository.countByMajorProfile_MajorProfileId(profileId);
		boolean isLiked =
			currentMemberId != 0L && majorProfileLikeRepository.existsByMajorProfile_MajorProfileIdAndMemberId(
				profileId, currentMemberId);

		return MajorProfileResponse.of(profile, academic, likeCount, isLiked);
	}

	@Transactional
	public LikeToggleResponse toggleLike(Long memberId, Long profileId) {
		MajorProfile profile = majorProfileRepository.findById(profileId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_404_PROFILE_REQUIRED));

		// 2. 이미 좋아요를 눌렀는지 확인 (조회)
		Optional<MajorProfileLike> existingLike =
			majorProfileLikeRepository.findByMajorProfile_MajorProfileIdAndMemberId(profileId, memberId);

		boolean isLikedNow;

		if (existingLike.isPresent()) {
			majorProfileLikeRepository.delete(existingLike.get());
			isLikedNow = false;
		} else {
			majorProfileLikeRepository.save(MajorProfileLike.createLike(profile, memberId));
			isLikedNow = true;

			// 알림 발송 로직을 여기에 넣으면 등록 시에만 알림이 갑니다.
		}

		long totalLikes = majorProfileLikeRepository.countByMajorProfile_MajorProfileId(profileId);

		return LikeToggleResponse.of(isLikedNow, totalLikes);
	}
}
