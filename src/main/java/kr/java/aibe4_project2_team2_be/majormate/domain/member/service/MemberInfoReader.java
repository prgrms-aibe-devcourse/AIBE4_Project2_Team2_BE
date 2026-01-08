package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoReader {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	public MemberProfile getProfileOrThrow(Long memberId) {
		return memberProfileRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	}

	@Transactional
	public MemberAcademic getOrCreateAcademic(Long memberId) {
		return memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElseGet(() -> createAcademic(memberId));
	}

	public void validateMajorRoleOrThrow(Long memberId) {
		MemberProfile profile = getProfileOrThrow(memberId);
		if (profile.getRole() != MemberRole.MAJOR) {
			throw new BusinessException(ErrorCode.MAJOR_ROLE_REQUIRED);
		}
	}

	private MemberAcademic createAcademic(Long memberId) {
		MemberProfile memberProfile = getProfileOrThrow(memberId);
		MemberAcademic memberAcademic = MemberAcademic.create(memberProfile);
		return memberAcademicRepository.save(memberAcademic);
	}
}
