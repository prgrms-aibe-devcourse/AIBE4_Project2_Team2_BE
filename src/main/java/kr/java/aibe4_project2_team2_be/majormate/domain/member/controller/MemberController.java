package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 API")
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

	@GetMapping("/{memberId}/profile")
	public ResponseEntity<ApiResponse<MemberProfileResponse>> getProfile(@PathVariable Long memberId) {
		MemberProfileResponse response = memberService.getProfileByMemberId(memberId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
	}
}
