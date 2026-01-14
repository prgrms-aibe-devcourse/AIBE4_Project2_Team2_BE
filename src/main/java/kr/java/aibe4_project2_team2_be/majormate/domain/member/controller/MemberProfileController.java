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
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.request.MemberProfileUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberProfileService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberProfileController {

	private final MemberProfileService memberProfileService;

	@GetMapping
	public ApiResponseNew<MemberProfileResponse> getMyProfile() {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.getMemberProfile(requesterId);
		return ApiResponseNew.success(response);
	}

	@PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiResponseNew<MemberProfileResponse> updateMyProfile(
		@Valid @RequestBody MemberProfileUpdateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.updateMemberProfile(requesterId, request);
		return ApiResponseNew.success(response);
	}

	@PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponseNew<MemberProfileResponse> updateMyProfileImage(
		@RequestPart("file") MultipartFile imgFile
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.updateMemberProfileImage(requesterId, imgFile);
		return ApiResponseNew.success(response);
	}

	@DeleteMapping("/profile-image")
	public ApiResponseNew<MemberProfileResponse> deleteMyProfileImage() {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		MemberProfileResponse response = memberProfileService.deleteMemberProfileImage(requesterId);
		return ApiResponseNew.success(response);
	}
}
