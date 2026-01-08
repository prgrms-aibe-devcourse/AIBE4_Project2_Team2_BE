package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberDetailUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberAcademicResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.DuplicateException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberProfileResponse getMemberProfile(Long memberId) {
		MemberProfile profile = getMemberProfileOrThrow(memberId);
		return MemberProfileResponse.from(profile);
	}

	public MemberAcademicResponse getMemberAcademic(Long memberId) {
		MemberAcademic academic = getMemberAcademicOrThrow(memberId);
		return MemberAcademicResponse.from(academic);
	}

	public MemberDetailResponse getMemberDetail(Long memberId) {
		MemberProfile profile = getMemberProfileOrThrow(memberId);
		MemberAcademic academic = getMemberAcademicOrThrow(memberId);
		return MemberDetailResponse.from(profile, academic);
	}

	@Transactional
	public MemberDetailResponse updateMemberDetail(Long memberId, MemberDetailUpdateRequest request) {
		MemberProfile profile = getMemberProfileOrThrow(memberId);
		MemberAcademic academic = getMemberAcademicOrThrow(memberId);

		validateCurrentPasswordOrThrow(profile, request.currentPassword());

		applyMemberUpdates(profile, request);
		applyAcademicUpdates(academic, request);

		return MemberDetailResponse.from(profile, academic);
	}

	private MemberProfile getMemberProfileOrThrow(Long memberId) {
		return memberProfileRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private MemberAcademic getMemberAcademicOrThrow(Long memberId) {
		return memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_ACADEMIC_NOT_FOUND));
	}

	private void validateCurrentPasswordOrThrow(MemberProfile profile, String currentPassword) {
		if (currentPassword == null || currentPassword.isBlank()) {
			throw new BadRequestException(ErrorCode.CURRENT_PASSWORD_REQUIRED);
		}
		if (!passwordEncoder.matches(currentPassword, profile.getPassword())) {
			throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void applyMemberUpdates(MemberProfile profile, MemberDetailUpdateRequest request) {
		updateNickname(profile, request.nickname());
		updateEmail(profile, request.email());
		updatePasswordIfProvided(profile, request.newPassword());
		updateProfileImageUrl(profile, request.profileImageUrl());
		updateStatus(profile, request.status());
	}

	private void updateNickname(MemberProfile profile, String nickname) {
		if (Objects.equals(nickname, profile.getNickname())) {
			return;
		}
		if (memberProfileRepository.existsByNickname(nickname)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
		}
		profile.updateNickname(nickname);
	}

	private void updateEmail(MemberProfile profile, String email) {
		if (Objects.equals(email, profile.getEmail())) {
			return;
		}
		if (memberProfileRepository.existsByEmail(email)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
		}
		profile.updateEmail(email);
	}

	private void updatePasswordIfProvided(MemberProfile profile, String newPassword) {
		if (newPassword == null || newPassword.isBlank()) {
			return;
		}
		if (passwordEncoder.matches(newPassword, profile.getPassword())) {
			throw new BadRequestException(ErrorCode.SAME_AS_OLD_PASSWORD);
		}
		profile.updatePassword(passwordEncoder.encode(newPassword));
	}

	private void updateProfileImageUrl(MemberProfile profile, String profileImageUrl) {
		if (profileImageUrl == null) {
			if (profile.getProfileImageUrl() != null) {
				profile.updateProfileImageUrl(null);
			}
			return;
		}
		if (profileImageUrl.isBlank()) {
			throw new BadRequestException(ErrorCode.INVALID_PROFILE_IMAGE_URL);
		}
		if (Objects.equals(profileImageUrl, profile.getProfileImageUrl())) {
			return;
		}
		profile.updateProfileImageUrl(profileImageUrl);
	}

	private void updateStatus(MemberProfile profile, MemberStatus status) {
		if (status == profile.getStatus()) {
			return;
		}
		profile.updateStatus(status);
	}

	private void applyAcademicUpdates(MemberAcademic academic, MemberDetailUpdateRequest request) {
		updateUniversity(academic, request.university());
		updateMajor(academic, request.major());
	}

	private void updateUniversity(MemberAcademic academic, String university) {
		if (Objects.equals(university, academic.getUniversity())) {
			return;
		}
		academic.updateUniversity(university);
	}

	private void updateMajor(MemberAcademic academic, String major) {
		if (Objects.equals(major, academic.getMajor())) {
			return;
		}
		academic.updateMajor(major);
	}
}
