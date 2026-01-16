package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.LikeToggleResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorCardResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response.MajorProfileResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service.MajorProfileService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/major-profiles")
@RequiredArgsConstructor
@Tag(name = "Major Profile", description = "전공자 프로필 API")
public class MajorProfileController {
	private final MajorProfileService majorProfileService;

	@Operation(summary = "전공자 프로필 생성", description = "전공자 프로필을 생성합니다.")
	@PostMapping
	public ApiResponse<Long> createProfile(
		@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Long profileId = majorProfileService.createProfile(memberId, majorProfileCreateRequest);

		return ApiResponse.success(profileId);
	}

	@Operation(summary = "전공자 프로필 수정", description = "전공자 프로필을 수정합니다.")
	@PatchMapping
	public ApiResponse<Void> updateProfile(@RequestBody @Valid MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.updateProfile(memberId, majorProfileCreateRequest);

		return ApiResponse.success(null);
	}

	@Operation(summary = "내 전공자 프로필 조회", description = "자신의 전공자 프로필을 조회합니다.")
	@GetMapping("/me")
	public ApiResponse<MajorProfileResponse> getMyProfile() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		MajorProfileResponse response = majorProfileService.getMyProfile(memberId);

		if (response == null) {
			return ApiResponse.success();
		}

		return ApiResponse.success(response);
	}

	@Operation(summary = "전공자 프로필 활성/비활성화", description = "전공자 프로필의 활성 상태를 토글합니다.")
	@PatchMapping("/status")
	public ApiResponse<Void> toggleProfileActive() {
		Long memberId = SecurityUtil.getCurrentMemberId();
		majorProfileService.toggleProfileActive(memberId);

		return ApiResponse.success(null);
	}

	@Operation(summary = "전공자 프로필 목록 조회", description = "전공자 프로필 목록을 조회합니다. (검색 및 페이징 지원)")
	@GetMapping
	public ApiResponse<Page<MajorCardResponse>> getMajorCards(
		@RequestParam(required = false) String searchType,
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 8, sort = "majorProfileId", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ApiResponse.success(majorProfileService.getMajorCards(searchType, keyword, pageable));
	}

	@Operation(summary = "전공자 프로필 상세 조회", description = "특정 전공자 프로필의 상세 정보를 조회합니다.")
	@GetMapping("/{profileId}")
	public ApiResponse<MajorProfileResponse> getMajorCardDetail(@PathVariable Long profileId) {
		MajorProfileResponse response = majorProfileService.getMajorCardDetail(profileId);
		return ApiResponse.success(response);
	}

	@Operation(summary = "전공자 프로필 좋아요 토글", description = "전공자 프로필에 좋아요를 누르거나 취소합니다.")
	@PostMapping("/{profileId}/likes")
	public ApiResponse<LikeToggleResponse> toggleLike(@PathVariable Long profileId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		LikeToggleResponse response = majorProfileService.toggleLike(memberId, profileId);
		return ApiResponse.success(response);
	}
}
