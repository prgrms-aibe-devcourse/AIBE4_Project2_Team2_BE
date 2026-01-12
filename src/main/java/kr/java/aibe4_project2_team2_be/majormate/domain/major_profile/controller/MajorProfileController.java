package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service.MajorProfileService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/major-profiles")
@RequiredArgsConstructor
public class MajorProfileController {
	private final MajorProfileService majorProfileService;

	@PostMapping
	public ApiResponse<Long> createProfile(
		@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = 3L;
		Long profileId = majorProfileService.createProfile(memberId, majorProfileCreateRequest);

		return ApiResponse.success(profileId);
	}

	@PatchMapping
	public ApiResponse<Void> updateProfile(@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.updateProfile(memberId, majorProfileCreateRequest);

		return ApiResponse.success(null);
	}

	@GetMapping("/me")
	public ApiResponse<MajorProfileResponse> getMyProfile() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MajorProfileResponse response = majorProfileService.getMyProfile(memberId);

		if (response == null) {
			return ApiResponse.success(null, "프로필이 존재하지 않습니다. 프로필을 생성해주세요.");
		}

		return ApiResponse.success(response);
	}

	@PatchMapping("/status")
	public ApiResponse<Void> toggleProfileActive() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.toggleProfileActive(memberId);

		return ApiResponse.success(null);
	}

	@GetMapping
	public ApiResponse<List<MajorCardResponse>> getMajorCards() {
		return ApiResponse.success(majorProfileService.getMajorCards());
	}

	@GetMapping("/{profileId}")
	public ApiResponse<MajorProfileResponse> getMajorCardDetail(@PathVariable Long profileId) {
		MajorProfileResponse response = majorProfileService.getMajorCardDetail(profileId);
		return ApiResponse.success(response);
	}
}
