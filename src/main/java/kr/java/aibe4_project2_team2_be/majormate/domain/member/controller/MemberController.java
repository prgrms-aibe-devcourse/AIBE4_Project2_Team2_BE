package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberInfoUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberInfoResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping
	public ApiResponseNew<MemberInfoResponse> getMyInfo() {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		MemberInfoResponse response = memberService.getMemberInfo(memberId);
		return ApiResponseNew.success(response);
	}

	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiResponseNew<MemberInfoResponse> updateMyInfo(@RequestBody @Valid MemberInfoUpdateRequest request) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		MemberInfoResponse response = memberService.updateMemberInfo(memberId, request);
		return ApiResponseNew.success(response);
	}
}
