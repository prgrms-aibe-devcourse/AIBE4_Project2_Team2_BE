package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberDetailUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberAcademicResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.DuplicateException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberInfoReader memberInfoReader;
	private final MemberProfileRepository memberProfileRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberProfileResponse getMemberProfile(Long memberId) {
		MemberProfile profile = memberInfoReader.getProfileOrThrow(memberId);
		return MemberProfileResponse.from(profile);
	}

	@Transactional
	public MemberAcademicResponse getMemberAcademic(Long memberId) {
		MemberAcademic academic = memberInfoReader.getOrCreateAcademic(memberId);
		return MemberAcademicResponse.from(academic);
	}

	@Transactional
	public MemberDetailResponse getMemberDetail(Long memberId) {
		MemberProfile profile = memberInfoReader.getProfileOrThrow(memberId);
		MemberAcademic academic = memberInfoReader.getOrCreateAcademic(memberId);
		return MemberDetailResponse.from(profile, academic);
	}

	@Transactional
	public MemberDetailResponse updateMemberDetail(Long memberId, MemberDetailUpdateRequest request) {
		MemberProfile profile = memberInfoReader.getProfileOrThrow(memberId);
		MemberAcademic academic = memberInfoReader.getOrCreateAcademic(memberId);

		validateCurrentPasswordOrThrow(profile, request.currentPassword());
		validateMajorAcademicRequiredOrThrow(profile, request.university(), request.major());

		applyMemberUpdates(profile, request);
		applyAcademicUpdates(academic, request);

		return MemberDetailResponse.from(profile, academic);
	}

	private void validateCurrentPasswordOrThrow(MemberProfile profile, String currentPassword) {
		if (isBlank(currentPassword)) {
			throw new BadRequestException(ErrorCode.CURRENT_PASSWORD_REQUIRED);
		}
		if (!passwordEncoder.matches(currentPassword, profile.getPassword())) {
			throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void validateMajorAcademicRequiredOrThrow(MemberProfile profile, String university, String major) {
		if (profile.getRole() != MemberRole.MAJOR) {
			return;
		}
		if (isBlank(university) || isBlank(major)) {
			throw new BusinessException(ErrorCode.MAJOR_ACADEMIC_REQUIRED);
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
		if (isBlank(newPassword)) {
			return;
		}
		if (passwordEncoder.matches(newPassword, profile.getPassword())) {
			throw new BadRequestException(ErrorCode.SAME_AS_OLD_PASSWORD);
		}
		profile.updatePassword(passwordEncoder.encode(newPassword));
	}

	private void updateProfileImageUrl(MemberProfile profile, String profileImageUrl) {
		if (isBlank(profileImageUrl)) {
			if (profile.getProfileImageUrl() != null) {
				profile.updateProfileImageUrl(null);
			}
			return;
		}
		if (Objects.equals(profileImageUrl, profile.getProfileImageUrl())) {
			return;
		}
		profile.updateProfileImageUrl(profileImageUrl);
	}

	private void updateStatus(MemberProfile profile, String status) {
		if (isBlank(status)) {
			return;
		}
		MemberStatus newStatus = parseStatusOrThrow(status.toUpperCase());
		if (newStatus == profile.getStatus()) {
			return;
		}
		profile.updateStatus(newStatus);
	}

	private MemberStatus parseStatusOrThrow(String status) {
		try {
			return MemberStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(ErrorCode.INVALID_MEMBER_STATUS);
		}
	}

	private void applyAcademicUpdates(MemberAcademic academic, MemberDetailUpdateRequest request) {
		updateUniversity(academic, request.university());
		updateMajor(academic, request.major());
	}

	private void updateUniversity(MemberAcademic academic, String university) {
		updateIfNotBlankAndChanged(university, academic.getUniversity(), academic::updateUniversity);
	}

	private void updateMajor(MemberAcademic academic, String major) {
		updateIfNotBlankAndChanged(major, academic.getMajor(), academic::updateMajor);
	}

	private void updateIfNotBlankAndChanged(String newValue, String currentValue, Consumer<String> updateAction) {
		if (isBlank(newValue)) {
			return;
		}
		if (Objects.equals(newValue, currentValue)) {
			return;
		}
		updateAction.accept(newValue);
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
