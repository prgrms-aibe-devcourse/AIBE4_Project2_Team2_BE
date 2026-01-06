package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberProfileUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.DuplicateException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberAcademicRepository memberAcademicRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberResponse getCurrentMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		log.info("현재 사용자 정보 조회 - ID: {}, Email: {}", member.getMemberId(), member.getEmail());

		return MemberResponse.from(member);
	}

	public MemberProfileResponse getProfile(Long memberId) {
		Member member = getMemberOrThrow(memberId);
		MemberAcademic academic = getAcademicOrThrow(member);
		return toProfileResponse(member, academic);
	}

	@Transactional
	public MemberProfileResponse updateProfile(Long memberId, MemberProfileUpdateRequest profile) {
		Member member = getMemberOrThrow(memberId);

		validateCurrentPassword(member, profile.currentPassword());

		applyMemberUpdates(member, profile);

		MemberAcademic academic = getAcademicOrThrow(member);
		applyAcademicUpdates(academic, profile);

		return toProfileResponse(member, academic);
	}

	private Member getMemberOrThrow(Long memberId) {
		return memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private MemberAcademic getAcademicOrThrow(Member member) {
		return memberAcademicRepository.findByMember(member)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_ACADEMIC_NOT_FOUND));
	}

	private void validateCurrentPassword(Member member, String currentPassword) {
		if (currentPassword == null || currentPassword.isBlank()) {
			throw new BadRequestException(ErrorCode.CURRENT_PASSWORD_REQUIRED);
		}
		if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
			throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private void applyMemberUpdates(Member member, MemberProfileUpdateRequest profile) {
		updateNickname(member, profile);
		updateEmail(member, profile);
		updatePasswordIfProvided(member, profile);
		updateProfileImageUrl(member, profile);
		updateStatus(member, profile);
	}

	private void updateNickname(Member member, MemberProfileUpdateRequest profile) {
		String nickname = profile.nickname();
		if (nickname.equals(member.getNickname())) {
			return;
		}
		if (memberRepository.existsByNickname(nickname)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
		}
		member.updateNickname(nickname);
	}

	private void updateEmail(Member member, MemberProfileUpdateRequest profile) {
		String email = profile.email();
		if (email.equals(member.getEmail())) {
			return;
		}
		if (memberRepository.existsByEmail(email)) {
			throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
		}
		member.updateEmail(email);
	}

	private void updatePasswordIfProvided(Member member, MemberProfileUpdateRequest profile) {
		String newPassword = profile.newPassword();
		if (newPassword == null || newPassword.isBlank()) {
			return;
		}
		if (passwordEncoder.matches(newPassword, member.getPassword())) {
			throw new BadRequestException(ErrorCode.SAME_AS_OLD_PASSWORD);
		}
		member.updatePassword(passwordEncoder.encode(newPassword));
	}

	private void updateProfileImageUrl(Member member, MemberProfileUpdateRequest profile) {
		String profileImageUrl = profile.profileImageUrl();
		if (profileImageUrl == null) {
			if (member.getProfileImageUrl() != null) {
				member.updateProfileImageUrl(null);
			}
			return;
		}
		if (profileImageUrl.isBlank()) {
			throw new BadRequestException(ErrorCode.INVALID_PROFILE_IMAGE_URL);
		}
		if (profileImageUrl.equals(member.getProfileImageUrl())) {
			return;
		}
		member.updateProfileImageUrl(profileImageUrl);
	}

	private void updateStatus(Member member, MemberProfileUpdateRequest profile) {
		MemberStatus status = profile.status();
		if (status == member.getStatus()) {
			return;
		}
		member.updateStatus(status);
	}

	private void applyAcademicUpdates(MemberAcademic academic, MemberProfileUpdateRequest profile) {
		updateUniversity(academic, profile);
		updateMajor(academic, profile);
	}

	private void updateUniversity(MemberAcademic academic, MemberProfileUpdateRequest profile) {
		String university = profile.university();
		if (university.equals(academic.getUniversity())) {
			return;
		}
		academic.updateUniversity(university);
	}

	private void updateMajor(MemberAcademic academic, MemberProfileUpdateRequest profile) {
		String major = profile.major();
		if (major.equals(academic.getMajor())) {
			return;
		}
		academic.updateMajor(major);
	}

	private MemberProfileResponse toProfileResponse(Member member, MemberAcademic academic) {
		return new MemberProfileResponse(
			member.getName(),
			member.getNickname(),
			member.getEmail(),
			member.getUsername(),
			member.getProfileImageUrl(),
			member.getStatus(),
			academic.getUniversity(),
			academic.getMajor()
		);
	}
}
