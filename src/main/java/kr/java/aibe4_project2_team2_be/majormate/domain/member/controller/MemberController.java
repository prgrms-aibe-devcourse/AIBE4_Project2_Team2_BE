package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberInfoUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberInfoResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping
	public ApiResponseNew<MemberInfoResponse> getMyInfo() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberInfoResponse response = memberService.getMemberInfo(memberId);
		return ApiResponseNew.success(response);
	}

	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiResponseNew<MemberInfoResponse> updateMyInfo(@RequestBody @Valid MemberInfoUpdateRequest request) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberInfoResponse response = memberService.updateMemberInfo(memberId, request);
		return ApiResponseNew.success(response);
	}

	@PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponseNew<MemberInfoResponse> updateProfileImage(@RequestPart("file") MultipartFile file) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberInfoResponse response = memberService.updateProfileImage(memberId, file);
		return ApiResponseNew.success(response);
	}

	@DeleteMapping("/profile-image")
	public ApiResponseNew<MemberInfoResponse> deleteProfileImage() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MemberInfoResponse response = memberService.deleteProfileImage(memberId);
		return ApiResponseNew.success(response);
	}
}
