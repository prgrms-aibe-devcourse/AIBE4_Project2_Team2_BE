package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberProfileUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 프로필 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 현재 로그인한 사용자의 정보를 조회합니다.")
	@GetMapping("/me")
	public ApiResponse<MemberResponse> getCurrentMember() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberResponse response = memberService.getCurrentMember(memberId);
		return ApiResponse.success(response, "사용자 정보 조회 성공");
	}

	@Operation(
		summary = "회원 프로필 조회",
		description = "memberId에 해당하는 회원의 프로필 정보를 조회합니다."
	)
	@GetMapping("/{memberId}/profile")
	public ApiResponse<MemberProfileResponse> getMyProfile(@PathVariable Long memberId) {
		MemberProfileResponse response = memberService.getProfile(memberId);
		return ApiResponse.success(response);
	}

	@Operation(
		summary = "회원 프로필 수정",
		description = "memberId에 해당하는 회원의 프로필 정보를 수정합니다."
			+ "currentPassword가 일치하지 않으면 요청이 거부됩니다. "
			+ "newPassword가 존재하면 비밀번호를 변경합니다."
			+ "profileImageUrl이 null이면 프로필 이미지를 삭제합니다."
	)
	@PatchMapping("/{memberId}/profile")
	public ApiResponse<MemberProfileResponse> updateMyProfile(
		@PathVariable Long memberId,
		@Parameter(description = "회원 프로필 수정 요청 바디")
		@RequestBody @Valid MemberProfileUpdateRequest request
	) {
		MemberProfileResponse response = memberService.updateProfile(memberId, request);
		return ApiResponse.success(response);
	}
}
