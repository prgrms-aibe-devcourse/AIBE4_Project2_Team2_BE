package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberInfoReader {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberAcademicRepository memberAcademicRepository;

	// ===== Profile =====

	public MemberProfile getProfileOrThrow(Long memberId) {
		return memberProfileRepository.findById(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MEMBER_404));
	}

	/**
	 * academic을 fetch join으로 같이 가져오는 조회.
	 * 주의: academic이 "없어서" 조회가 실패하면 안 되므로,
	 * findWithAcademicByMemberId는 LEFT JOIN FETCH로 구현되어 있어야 한다.
	 */
	public MemberProfile getProfileWithAcademicOrThrow(Long memberId) {
		return memberProfileRepository.findWithAcademicByMemberId(memberId)
			.orElseThrow(() -> new BusinessExceptionNew(ErrorCodeNew.MEMBER_404));
	}

	// ===== Academic 조회/생성 =====

	/**
	 * academic을 조회만 한다. 없으면 null을 반환한다.
	 * 조회 API에서 academic을 "자동 생성"하지 않도록 하기 위한 메서드다.
	 */
	public MemberAcademic getAcademicOrNull(Long memberId) {
		return memberAcademicRepository.findByMemberProfile_MemberId(memberId)
			.orElse(null);
	}

	/**
	 * profile 기준으로 academic을 조회한다. 없으면 null.
	 */
	public MemberAcademic getAcademicOrNull(MemberProfile profile) {
		Objects.requireNonNull(profile, "profile must not be null");
		return profile.getAcademic();
	}

	/**
	 * academic이 없으면 생성한다. (업데이트/전공 신청 등에서 사용)
	 * profile.attachAcademic()이 양방향을 동기화한다고 가정한다.
	 */
	@Transactional
	public MemberAcademic createAcademicIfAbsent(MemberProfile profile) {
		Objects.requireNonNull(profile, "profile must not be null");

		if (profile.getAcademic() != null) {
			return profile.getAcademic();
		}

		MemberAcademic academic = MemberAcademic.create(profile);
		profile.attachAcademic(academic);
		return memberAcademicRepository.save(academic);
	}

	/**
	 * memberId로 profile을 가져오고 academic이 없으면 생성해서 반환한다.
	 * 다른 도메인에서 가장 자주 쓰일 형태다.
	 */
	@Transactional
	public MemberProfile getProfileWithAcademicCreatedIfAbsentOrThrow(Long memberId) {
		MemberProfile profile = getProfileWithAcademicOrThrow(memberId);
		createAcademicIfAbsent(profile);
		return profile;
	}

	/**
	 * academic이 반드시 있어야 하는 케이스에서 사용한다.
	 * 없으면 생성하는 방식이 아니라, 정책 위반으로 처리한다.
	 * 예: 스냅샷에 학적/학교/학과를 반드시 남겨야 하는 업무 흐름 등
	 */
	public MemberAcademic getAcademicOrThrow(MemberProfile profile, ErrorCodeNew errorCodeIfMissing) {
		Objects.requireNonNull(profile, "profile must not be null");
		if (profile.getAcademic() == null) {
			throw new BusinessExceptionNew(errorCodeIfMissing);
		}
		return profile.getAcademic();
	}

	// ===== Role/정책 검증 =====

	public void validateMajorRoleOrThrow(Long memberId) {
		MemberProfile profile = getProfileOrThrow(memberId);
		validateMajorRoleOrThrow(profile);
	}

	public void validateMajorRoleOrThrow(MemberProfile profile) {
		Objects.requireNonNull(profile, "profile must not be null");
		if (profile.getRole() != MemberRole.MAJOR) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_403_ROLE_REQUIRED);
		}
	}

	/**
	 * 전공자(MAJOR)로서 동작하는 기능에서 학적 정보가 갖춰져야 하는 정책 검증.
	 * major_profile 공개, 인터뷰 수락/처리, 전공자 신청 처리 등에서 재사용 가능하다.
	 *
	 * status는 현재 MemberProfile에 있다고 가정하고, 정책상 null이면 에러로 처리한다.
	 */
	public void validateMajorAcademicRequiredOrThrow(MemberProfile major) {
		Objects.requireNonNull(major, "major must not be null");

		if (major.getRole() != MemberRole.MAJOR) {
			return;
		}

		if (major.getStatus() == null) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_STATUS_REQUIRED);
		}

		MemberAcademic academic = major.getAcademic();
		if (academic == null || isBlank(academic.getUniversity()) || isBlank(academic.getMajor())) {
			throw new BusinessExceptionNew(ErrorCodeNew.MAJOR_400_ACADEMIC_REQUIRED);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
