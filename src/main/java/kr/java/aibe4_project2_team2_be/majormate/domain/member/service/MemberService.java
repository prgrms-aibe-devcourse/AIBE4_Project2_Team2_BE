package kr.java.aibe4_project2_team2_be.majormate.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberAcademicRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
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

	public MemberResponse getCurrentMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		log.info("현재 사용자 정보 조회 - ID: {}, Email: {}", member.getMemberId(), member.getEmail());

		return MemberResponse.from(member);
	}

	public MemberProfileResponse getProfileByMemberId(Long memberId) {
		Member member = memberRepository.findByMemberId(memberId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		MemberAcademic academic = memberAcademicRepository.findByMember(member);

		return new MemberProfileResponse(
			member.getName(),
			member.getNickname(),
			member.getUsername(),
			member.getProfileImageUrl(),
			academic.getUniversity(),
			academic.getMajor()
		);
	}
}
