package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
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
}
