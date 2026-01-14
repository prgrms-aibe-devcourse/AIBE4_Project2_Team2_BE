package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberProfileUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.service.S3FileService;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

	private final MemberInfoReader memberInfoReader;
	private final MemberProfileRepository memberProfileRepository;

	private final S3FileService s3FileService;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public MemberProfileResponse getMemberProfile(Long memberId) {
		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);
		return MemberProfileResponse.from(profile);
	}

	@Transactional
	public MemberProfileResponse updateMemberProfile(Long memberId, MemberProfileUpdateRequest request) {
		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		updateNickname(request.nickname(), profile);

		updatePassword(request.currentPassword(), request.newPassword(), profile);

		updateStatus(request.status(), profile);

		MemberAcademic academic = resolveAcademicForUpdate(request, profile);
		applyAcademicUpdates(request, academic);

		return MemberProfileResponse.from(profile);
	}

	@Transactional
	public MemberProfileResponse updateMemberProfileImage(Long memberId, MultipartFile imgFile) {
		if (imgFile == null || imgFile.isEmpty()) {
			throw new BusinessException(ErrorCode.MEMBER_400_PROFILE_IMAGE_FILE_MISSING);
		}

		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		String oldUrl = profile.getProfileImageUrl();
		String newUrl = s3FileService.upload(imgFile);

		if (StringUtils.hasText(oldUrl)) {
			s3FileService.delete(oldUrl);
		}

		profile.updateProfileImageUrl(newUrl);

		return MemberProfileResponse.from(profile);
	}

	@Transactional
	public MemberProfileResponse deleteMemberProfileImage(Long memberId) {
		MemberProfile profile = memberInfoReader.getProfileWithAcademicOrThrow(memberId);

		String oldUrl = profile.getProfileImageUrl();

		if (StringUtils.hasText(oldUrl)) {
			s3FileService.delete(oldUrl);
			profile.updateProfileImageUrl(null);
		}

		return MemberProfileResponse.from(profile);
	}

	private void updateNickname(String nickname, MemberProfile profile) {
		if (Objects.equals(nickname, profile.getNickname())) {
			return;
		}
		if (memberProfileRepository.existsByNickname(nickname)) {
			throw new BusinessException(ErrorCode.MEMBER_409_DUPLICATE_NICKNAME);
		}
		profile.updateNickname(nickname);
	}

	private void updatePassword(String currentPassword, String newPassword, MemberProfile profile) {
		boolean hasCurrent = !isBlank(currentPassword);
		boolean hasNew = !isBlank(newPassword);

		// 1) OAuth2는 비밀번호 입력 자체가 금지
		if (profile.isOAuth2User()) {
			if (hasCurrent || hasNew) {
				throw new BusinessException(ErrorCode.MEMBER_400_CURRENT_PASSWORD_NOT_ALLOWED);
			}
			return;
		}

		// 2) 둘 다 없으면 변경 없음
		if (!hasCurrent && !hasNew) {
			return;
		}

		// 3) 둘 중 하나만 오면 예외 (둘 다 필수)
		if (!hasCurrent || !hasNew) {
			throw new BusinessException(ErrorCode.MEMBER_400_PASSWORD_BOTH_REQUIRED);
		}

		// 4) 현재 비밀번호 검증
		if (!profile.hasPassword() || !passwordEncoder.matches(currentPassword, profile.getPassword())) {
			throw new BusinessException(ErrorCode.MEMBER_400_CURRENT_PASSWORD_MISMATCH);
		}

		// 5) 새 비밀번호가 기존 비밀번호와 동일하면 예외
		if (passwordEncoder.matches(newPassword, profile.getPassword())) {
			throw new BusinessException(ErrorCode.MEMBER_400_SAME_AS_OLD_PASSWORD);
		}

		profile.updatePassword(passwordEncoder.encode(newPassword));
	}

	private void updateStatus(MemberStatus status, MemberProfile profile) {
		if (profile.getRole() == MemberRole.MAJOR
			&& status != MemberStatus.ENROLLED && status != MemberStatus.GRADUATED
		) {
			throw new BusinessException(ErrorCode.MAJOR_400_INVALID_STATUS);
		}

		if (status == profile.getStatus()) {
			return;
		}

		profile.updateStatus(status);
	}

	private MemberAcademic resolveAcademicForUpdate(MemberProfileUpdateRequest request, MemberProfile profile) {
		// 현재 프로필에 연결된 학적 정보 조회 (없으면 null)
		MemberAcademic academic = profile.getAcademic();

		// 요청에서 학적 필드(대학교/학과) 중 하나라도 포함되면 학적 업데이트를 요청한 것으로 간주
		// null 여부로만 판단하므로 빈 문자열("")도 "요청함"에 포함될 수 있다.
		boolean academicUpdateRequested = request.university() != null || request.major() != null;
		// MAJOR 역할은 학적 정보가 반드시 필요하다.
		boolean majorRequiresAcademic = profile.getRole() == MemberRole.MAJOR;

		// 학적이 없는데 학적 업데이트 요청이 있거나, MAJOR라서 학적이 필수라면
		// 학적 엔티티를 생성해두고 이후 업데이트/검증 로직이 진행되게 한다.
		if (academic == null && (academicUpdateRequested || majorRequiresAcademic)) {
			memberInfoReader.createAcademicIfAbsent(profile);
			academic = profile.getAcademic();
		}

		// MAJOR 역할이라면 최종적으로 university/major가 모두 채워져 있어야 한다.
		if (profile.getRole() == MemberRole.MAJOR) {
			String finalUniversity = resolveFinalValue(
				academic != null ? academic.getUniversity() : null, request.university()
			);
			String finalMajor = resolveFinalValue(
				academic != null ? academic.getMajor() : null, request.major()
			);
			if (isBlank(finalUniversity) || isBlank(finalMajor)) {
				throw new BusinessException(ErrorCode.MAJOR_400_ACADEMIC_REQUIRED);
			}
		}

		return academic;
	}

	private void applyAcademicUpdates(MemberProfileUpdateRequest request, MemberAcademic academic) {
		if (academic == null) {
			return;
		}
		updateUniversity(request.university(), academic);
		updateMajor(request.major(), academic);
	}

	private void updateUniversity(String university, MemberAcademic academic) {
		applyNullablePatch(university, academic.getUniversity(), academic::updateUniversity);
	}

	private void updateMajor(String major, MemberAcademic academic) {
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
