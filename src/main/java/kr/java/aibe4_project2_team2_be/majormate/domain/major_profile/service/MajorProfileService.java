package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.repository.MajorProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.ForbiddenException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorProfileService {
	private final MajorProfileRepository majorProfileRepository;
	private final MemberRepository memberRepository;

	public Long createProfile(Long memberId, MajorProfileCreateRequest majorProfileCreateRequest) {
		Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 회원입니다"));

		if(member.getRole() != MemberRole.MAJOR) throw new ForbiddenException(ErrorCode.ACCESS_DENIED);

		MajorProfile majorProfile = MajorProfile.createProfile(
			member,
			majorProfileCreateRequest.getTitle(),
			majorProfileCreateRequest.getContent(),
			majorProfileCreateRequest.getTags()
		);

		return majorProfileRepository.save(majorProfile).getMajorProfileId();
	}
}
