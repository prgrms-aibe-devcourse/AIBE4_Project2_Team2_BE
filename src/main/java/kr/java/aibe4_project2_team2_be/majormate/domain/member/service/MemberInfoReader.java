package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoReader {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	public MemberProfile getProfileOrThrow(Long memberId) {
		return memberProfileRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_404));
	}

	public MemberProfile getProfileWithAcademicOrThrow(Long memberId) {
		return memberProfileRepository.findWithAcademicByMemberId(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_404));
	}

	public MemberAcademic getAcademicOrNull(Long memberId) {
		return memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElse(null);
	}

	@Transactional
	public void createAcademicIfAbsent(MemberProfile profile) {
		Objects.requireNonNull(profile, "profile must not be null");
		if (profile.getAcademic() != null) {
			return;
		}

		MemberAcademic academic = MemberAcademic.create(profile);
		profile.attachAcademic(academic);
		memberAcademicRepository.save(academic);
	}

	public void validateMajorRoleOrThrow(Long memberId) {
		MemberProfile profile = getProfileOrThrow(memberId);
		if (profile.getRole() != MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.MAJOR_403_ROLE_REQUIRED);
		}
	}
}
