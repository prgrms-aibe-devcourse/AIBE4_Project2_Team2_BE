package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberInfoUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberInfoResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberInfoReader memberInfoReader;
	private final MemberProfileRepository memberProfileRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberInfoResponse getMemberInfo(Long memberId) {
		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);
		return MemberInfoResponse.from(profile);
	}

	@Transactional
	public MemberInfoResponse updateMemberInfo(Long memberId, MemberInfoUpdateRequest request) {
		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		validateCurrentPasswordByPolicyOrThrow(profile, request.currentPassword());
		validatePasswordChangePolicyOrThrow(profile, request.newPassword());

		updateNickname(profile, request.nickname());
		updateEmail(profile, request.email());

		updatePasswordIfProvided(profile, request.newPassword());
		updateProfileImageUrl(profile, request.profileImageUrl());
		updateStatus(profile, request.status());

		MemberAcademic academic = resolveAcademicForUpdate(profile, request);
		applyAcademicUpdates(academic, request);

		return MemberInfoResponse.from(profile);
	}

	private void validateCurrentPasswordByPolicyOrThrow(MemberProfile profile, String currentPassword) {
		if (!profile.isLocalUser()) {
			if (currentPassword != null && !currentPassword.isBlank()) {
				throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_CURRENT_PASSWORD_NOT_ALLOWED);
			}
			return;
		}

		if (isBlank(currentPassword)) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_CURRENT_PASSWORD_REQUIRED);
		}
		if (!profile.hasPassword()) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_PASSWORD_REQUIRED);
		}
		if (!passwordEncoder.matches(currentPassword, profile.getPassword())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_CURRENT_PASSWORD_MISMATCH);
		}
	}

	private void validatePasswordChangePolicyOrThrow(MemberProfile profile, String newPassword) {
		if (isBlank(newPassword)) {
			return;
		}
		if (!profile.isLocalUser()) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_PASSWORD_CHANGE_NOT_ALLOWED);
		}
	}

	private void updateNickname(MemberProfile profile, String nickname) {
		if (Objects.equals(nickname, profile.getNickname())) {
			return;
		}
		if (memberProfileRepository.existsByNickname(nickname)) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_409_DUPLICATE_NICKNAME);
		}
		profile.updateNickname(nickname);
	}

	private void updateEmail(MemberProfile profile, String email) {
		if (Objects.equals(email, profile.getEmail())) {
			return;
		}
		if (memberProfileRepository.existsByEmail(email)) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_409_DUPLICATE_EMAIL);
		}
		profile.updateEmail(email);
	}

	private void updatePasswordIfProvided(MemberProfile profile, String newPassword) {
		if (isBlank(newPassword)) {
			return;
		}
		if (profile.hasPassword() && passwordEncoder.matches(newPassword, profile.getPassword())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MEMBER_400_SAME_AS_OLD_PASSWORD);
		}
		profile.updatePassword(passwordEncoder.encode(newPassword));
	}

	private void updateProfileImageUrl(MemberProfile profile, String profileImageUrl) {
		if (profileImageUrl == null) {
			return;
		}
		if (profileImageUrl.isBlank()) {
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

	private void updateStatus(MemberProfile profile, MemberStatus status) {
		if (status == null) {
			return;
		}
		if (status == profile.getStatus()) {
			return;
		}
		profile.updateStatus(status);
	}

	private MemberAcademic resolveAcademicForUpdate(MemberProfile profile, MemberInfoUpdateRequest request) {
		MemberAcademic academic = profile.getAcademic();

		boolean academicUpdateRequested = request.university() != null || request.major() != null;
		boolean majorRequiresAcademic = profile.getRole() == MemberRole.MAJOR;

		if (academic == null && (academicUpdateRequested || majorRequiresAcademic)) {
			memberInfoReader.createAcademicIfAbsent(profile);
			academic = profile.getAcademic();
		}

		if (profile.getRole() == MemberRole.MAJOR) {
			String finalUniversity = resolveFinalValue(
				academic != null ? academic.getUniversity() : null,
				request.university()
			);
			String finalMajor = resolveFinalValue(
				academic != null ? academic.getMajor() : null,
				request.major()
			);

			if (isBlank(finalUniversity) || isBlank(finalMajor)) {
				throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_ACADEMIC_REQUIRED);
			}
		}

		return academic;
	}

	private void applyAcademicUpdates(MemberAcademic academic, MemberInfoUpdateRequest request) {
		if (academic == null) {
			return;
		}
		updateUniversity(academic, request.university());
		updateMajor(academic, request.major());
	}

	private void updateUniversity(MemberAcademic academic, String university) {
		applyNullablePatch(university, academic.getUniversity(), academic::updateUniversity);
	}

	private void updateMajor(MemberAcademic academic, String major) {
		applyNullablePatch(major, academic.getMajor(), academic::updateMajor);
	}

	private void applyNullablePatch(String newValue, String currentValue, Consumer<String> updater) {
		if (newValue == null) {
			return;
		}
		if (newValue.isBlank()) {
			if (currentValue != null) {
				updater.accept(null);
			}
			return;
		}
		if (Objects.equals(newValue, currentValue)) {
			return;
		}
		updater.accept(newValue);
	}

	private String resolveFinalValue(String current, String requestValue) {
		if (requestValue == null) {
			return current;
		}
		if (requestValue.isBlank()) {
			return null;
		}
		return requestValue;
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
