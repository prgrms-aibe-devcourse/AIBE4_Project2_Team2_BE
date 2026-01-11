package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoReader {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	public MemberProfile getProfileOrThrow(Long memberId) {
		return memberProfileRepository.findById(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MEMBER_404));
	}

	public MemberProfile getProfileWithAcademicOrThrow(Long memberId) {
		return memberProfileRepository.findWithAcademicByMemberId(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MEMBER_404));
	}

	@Transactional
	public void createAcademicIfAbsent(MemberProfile profile) {
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
			throw new BusinessException(ErrorCode.MAJOR_ROLE_REQUIRED);
		}
	}
}
