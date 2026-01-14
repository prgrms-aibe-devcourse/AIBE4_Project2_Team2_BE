package kr.java.aibe4_project2_team2_be.majormate.domain.interview.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewStatusUpdateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.InterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.service.InterviewFormService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.InterviewFormStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.PageSort;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewFormController {

	private final InterviewFormService interviewFormService;

	/**
	 * /api/members/me/interviews?type=APPLIED
	 * /api/members/me/interviews?type=RECEIVED
	 * /api/members/me/interviews?type=APPLIED&status=COMPLETED&reviewed=false
	 **/
	@GetMapping("/members/me/interviews")
	public ApiResponseNew<List<InterviewFormResponse>> getMyInterviewForms(
		@RequestParam(name = "type") InterviewFormResponse.ViewType type,
		@RequestParam(name = "status", required = false) InterviewFormStatus status,
		@RequestParam(name = "reviewed", required = false) Boolean reviewed,
		@RequestParam(name = "sort", required = false, defaultValue = "CREATED_AT_DESC") PageSort sort,
		Pageable pageable
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		Page<InterviewFormResponse> response = interviewFormService.getMyInterviewForms(
			requesterId, type, status, reviewed, sort, pageable
		);
		return PageResponsesNew.of(response);
	}

	@GetMapping("/members/me/interviews/{interviewId}")
	public ApiResponseNew<InterviewFormResponse> getMyInterviewFormDetail(@PathVariable Long interviewId) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		InterviewFormResponse response = interviewFormService.getMyInterviewFormDetail(requesterId, interviewId);
		return ApiResponseNew.success(response);
	}

	@PostMapping("/majors/{majorId}/interviews")
	public ApiResponseNew<InterviewFormResponse> createInterviewForm(
		@PathVariable Long majorId,
		@Valid @RequestBody InterviewFormCreateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		InterviewFormResponse response = interviewFormService.createInterviewForm(requesterId, majorId, request);
		return ApiResponseNew.success(response);
	}

	/**
	 * body: { "status": "ACCEPTED", "majorMessage": "..." }
	 * body: { "status": "REJECTED", "majorMessage": "..." }
	 * body: { "status": "COMPLETED" }
	 **/
	@PatchMapping("/interviews/{interviewId}/status")
	public ApiResponseNew<Void> updateInterviewFormStatus(
		@PathVariable Long interviewId,
		@Valid @RequestBody InterviewStatusUpdateRequest request
	) {
		Long requesterId = SecurityUtil.getCurrentMemberId();
		interviewFormService.updateInterviewFormStatus(requesterId, interviewId, request);
		return ApiResponseNew.success();
	}
}
