package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorProfileService {
	private final MajorProfileRepository majorProfileRepository;
	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	public Long createProfile(Long memberId, MajorProfileCreateRequest request) {
		MemberProfile memberProfile = memberProfileRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));

		if (memberProfile.getRole() != MemberRole.MAJOR) {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}
		
		// 이미 프로필이 있는지 확인
		if (majorProfileRepository.findByMemberProfile_MemberId(memberId).isPresent()) {
			throw new IllegalStateException("이미 프로필이 존재합니다.");
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
			.orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));

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

		MemberAcademic academic = memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new EntityNotFoundException("학적 정보를 찾을 수 없습니다."));
		
		return MajorProfileResponse.of(profile, academic);
	}

	public void toggleProfileActive(Long memberId) {
		MajorProfile profile = majorProfileRepository.findByMemberProfile_MemberId(memberId).orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));
		profile.toggleActive();
	}

	@Transactional(readOnly = true)
	public List<MajorCardResponse> getMajorCards() {
		// 1. 활성화된 프로필 목록 조회 (MemberProfile, Tags Fetch Join)
		List<MajorProfile> profiles = majorProfileRepository.findAllByIsActiveTrue();

		if (profiles.isEmpty()) {
			return List.of();
		}

		// 2. memberId 목록 추출
		List<Long> memberIds = profiles.stream()
			.map(p -> p.getMemberProfile().getMemberId())
			.collect(Collectors.toList());

		// 3. 학적 정보 일괄 조회 (IN 쿼리)
		List<MemberAcademic> academics = memberAcademicRepository.findAllByMemberProfile_MemberIdIn(memberIds);

		// 4. 학적 정보를 Map으로 변환 (key: memberId, value: MemberAcademic)
		Map<Long, MemberAcademic> academicMap = academics.stream()
			.collect(Collectors.toMap(
				a -> a.getMemberProfile().getMemberId(),
				Function.identity()
			));

		// 5. DTO 변환
		return profiles.stream()
			.map(profile -> {
				MemberAcademic academic = academicMap.get(profile.getMemberProfile().getMemberId());
				// 학적 정보가 없는 경우 예외 처리 또는 기본값 처리 (여기서는 예외 발생)
				if (academic == null) {
					throw new EntityNotFoundException("학적 정보가 누락된 회원이 있습니다. ID: " + profile.getMemberProfile().getMemberId());
				}
				return MajorCardResponse.of(profile, academic);
			})
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public MajorProfileResponse getMajorCardDetail(Long profileId) {
		MajorProfile profile = majorProfileRepository.findById(profileId)
			.orElseThrow(() -> new EntityNotFoundException("해당 프로필을 찾을 수 없습니다."));

		if (!profile.isActive()) {
			throw new EntityNotFoundException("비활성화된 프로필입니다.");
		}

		Long memberId = profile.getMemberProfile().getMemberId();
		MemberAcademic academic = memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new EntityNotFoundException("학적 정보를 찾을 수 없습니다."));

		return MajorProfileResponse.of(profile, academic);
	}
}
