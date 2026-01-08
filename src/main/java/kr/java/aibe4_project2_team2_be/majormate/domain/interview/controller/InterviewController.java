package kr.java.aibe4_project2_team2_be.majormate.domain.interview.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.AppliedInterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.ReceivedInterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.service.InterviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewController {

	private final InterviewService interviewService;

	@GetMapping("/members/me/interviews/applied")
	public ApiResponse<List<AppliedInterviewFormResponse>> getMyAppliedInterviewForms() {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 9L;
		List<AppliedInterviewFormResponse> response = interviewService.getAppliedInterviewForms(studentId);
		if (response.isEmpty()) {
			return ApiResponse.success(response, "신청한 인터뷰가 없습니다.");
		}
		return ApiResponse.success(response);
	}

	@GetMapping("/members/me/interviews/received")
	public ApiResponse<List<ReceivedInterviewFormResponse>> getMyReceivedInterviewForms() {
		//Long majorId = SecurityUtil.getCurrentMemberId();
		Long majorId = 4L;
		List<ReceivedInterviewFormResponse> response = interviewService.getReceivedInterviewForms(majorId);
		if (response.isEmpty()) {
			return ApiResponse.success(response, "신청받은 인터뷰가 없습니다.");
		}
		return ApiResponse.success(response);
	}

	@PostMapping("/majors/{majorId}/interviews")
	public ApiResponse<AppliedInterviewFormResponse> createInterviewForm(
		@PathVariable Long majorId,
		@Valid @RequestBody InterviewFormCreateRequest request
	) {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 9L;
		AppliedInterviewFormResponse response = interviewService.createInterviewForm(studentId, majorId, request);
		return ApiResponse.success(response);
	}
}
