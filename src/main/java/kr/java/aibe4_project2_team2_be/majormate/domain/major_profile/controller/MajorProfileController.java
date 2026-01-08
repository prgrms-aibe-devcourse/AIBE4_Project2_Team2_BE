package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request.MajorProfileCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.service.MajorProfileService;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
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
		MajorProfileCreateRequest majorProfileCreateRequest
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Long profileId = majorProfileService.createProfile(memberId, majorProfileCreateRequest);

		return ApiResponse.success(profileId);
	}
}
