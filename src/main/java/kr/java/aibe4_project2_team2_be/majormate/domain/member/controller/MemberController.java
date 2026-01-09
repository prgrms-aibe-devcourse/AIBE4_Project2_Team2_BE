package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberDetailUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberAcademicResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberDetailResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping
	public ApiResponse<MemberProfileResponse> getMyProfile() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberService.getMemberProfile(memberId);
		return ApiResponse.success(response);
	}

	@GetMapping("/academic")
	public ApiResponse<MemberAcademicResponse> getMyAcademic() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberAcademicResponse response = memberService.getMemberAcademic(memberId);
		return ApiResponse.success(response);
	}

	@GetMapping("/detail")
	public ApiResponse<MemberDetailResponse> getMyDetail() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberDetailResponse response = memberService.getMemberDetail(memberId);
		return ApiResponse.success(response);
	}

	@PatchMapping("/detail")
	public ApiResponse<MemberDetailResponse> updateMyDetail(@RequestBody @Valid MemberDetailUpdateRequest request) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberDetailResponse response = memberService.updateMemberDetail(memberId, request);
		return ApiResponse.success(response);
	}
}
