package kr.java.aibe4_project2_team2_be.majormate.domain.interview.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.InterviewCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.InterviewResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.service.InterviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;

@RestController
public class InterviewController {

	private final InterviewService interviewService;

	public InterviewController(InterviewService interviewService) {
		this.interviewService = interviewService;
	}

	@GetMapping("/api/members/me/interviews")
	public ApiResponse<List<InterviewResponse>> getInterviews() {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 1L;
		List<InterviewResponse> response = interviewService.getInterviews(studentId);
		return ApiResponse.success(response);
	}

	@PostMapping("/api/majors/{majorId}/interview-requests")
	public ResponseEntity<Void> createInterview(
		@PathVariable Long majorId,
		@Valid @RequestBody InterviewCreateRequest request
	) {
		//Long studentId = SecurityUtil.getCurrentMemberId();
		Long studentId = 1L;
		interviewService.createInterview(studentId, majorId, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
