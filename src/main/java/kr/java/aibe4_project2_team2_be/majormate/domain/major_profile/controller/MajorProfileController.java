package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.LikeToggleResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service.MajorProfileService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/major-profiles")
@RequiredArgsConstructor
public class MajorProfileController {
	private final MajorProfileService majorProfileService;

	@PostMapping
	public ApiResponseNew<Long> createProfile(
		@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Long profileId = majorProfileService.createProfile(memberId, majorProfileCreateRequest);

		return ApiResponseNew.success(profileId);
	}

	@PatchMapping
	public ApiResponseNew<Void> updateProfile(@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.updateProfile(memberId, majorProfileCreateRequest);

		return ApiResponseNew.success(null);
	}

	@GetMapping("/me")
	public ApiResponseNew<MajorProfileResponse> getMyProfile() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MajorProfileResponse response = majorProfileService.getMyProfile(memberId);

		if (response == null) {
			return ApiResponseNew.success();
		}

		return ApiResponseNew.success(response);
	}

	@PatchMapping("/status")
	public ApiResponseNew<Void> toggleProfileActive() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.toggleProfileActive(memberId);

		return ApiResponseNew.success(null);
	}

	@GetMapping
	public ApiResponseNew<Page<MajorCardResponse>> getMajorCards(
		@RequestParam(required = false) String searchType,
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 8, sort = "majorProfileId", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ApiResponseNew.success(majorProfileService.getMajorCards(searchType, keyword ,pageable));
	}

	@GetMapping("/{profileId}")
	public ApiResponseNew<MajorProfileResponse> getMajorCardDetail(@PathVariable Long profileId) {
		MajorProfileResponse response = majorProfileService.getMajorCardDetail(profileId);
		return ApiResponseNew.success(response);
	}

	@PostMapping("/{profileId}/likes")
	public ApiResponseNew<LikeToggleResponse> toggleLike(@PathVariable Long profileId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		LikeToggleResponse response = majorProfileService.toggleLike(memberId, profileId);
		return ApiResponseNew.success(response);
	}
}
