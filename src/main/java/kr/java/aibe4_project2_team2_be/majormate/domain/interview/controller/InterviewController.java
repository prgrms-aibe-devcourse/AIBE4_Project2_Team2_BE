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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewDecisionRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.request.InterviewFormCreateRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.AppliedInterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.dto.response.ReceivedInterviewFormResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.interview.service.InterviewService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewController {

	private final InterviewService interviewService;

	@GetMapping("/members/me/interviews/applied")
	public ApiResponseNew<List<AppliedInterviewFormResponse>> getMyAppliedInterviewForms(Pageable pageable) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		Page<AppliedInterviewFormResponse> page = interviewService.getAppliedInterviewForms(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/interviews/applied/completed")
	public ApiResponseNew<List<AppliedInterviewFormResponse>> getMyAppliedCompletedInterviewForms(Pageable pageable) {
		// Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		Page<AppliedInterviewFormResponse> page = interviewService.getAppliedCompletedInterviewForms(memberId,
			pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/interviews/applied/{interviewId}")
	public ApiResponseNew<AppliedInterviewFormResponse> getMyAppliedInterviewFormDetail(
		@PathVariable Long interviewId) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		AppliedInterviewFormResponse response = interviewService.getAppliedInterviewFormDetail(memberId, interviewId);
		return ApiResponseNew.success(response);
	}

	@GetMapping("/members/me/interviews/received")
	public ApiResponseNew<List<ReceivedInterviewFormResponse>> getMyReceivedInterviewForms(
		Pageable pageable) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		Page<ReceivedInterviewFormResponse> page = interviewService.getReceivedInterviewForms(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/interviews/received/{interviewId}")
	public ApiResponseNew<ReceivedInterviewFormResponse> getMyReceivedInterviewFormDetail(
		@PathVariable Long interviewId) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		ReceivedInterviewFormResponse response = interviewService.getReceivedInterviewFormDetail(memberId, interviewId);
		return ApiResponseNew.success(response);
	}

	@PostMapping("/majors/{majorId}/interviews")
	public ApiResponseNew<AppliedInterviewFormResponse> createInterviewForm(
		@PathVariable Long majorId,
		@Valid @RequestBody InterviewFormCreateRequest request
	) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 6L;
		AppliedInterviewFormResponse response = interviewService.createInterviewForm(memberId, majorId, request);
		return ApiResponseNew.success(response);
	}

	@PatchMapping("/interviews/{interviewId}/accept")
	public ApiResponseNew<Void> accept(
		@PathVariable Long interviewId,
		@Valid @RequestBody InterviewDecisionRequest request
	) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		interviewService.accept(memberId, interviewId, request.message());
		return ApiResponseNew.success();
	}

	@PatchMapping("/interviews/{interviewId}/reject")
	public ApiResponseNew<Void> reject(
		@PathVariable Long interviewId,
		@Valid @RequestBody InterviewDecisionRequest request
	) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		interviewService.reject(memberId, interviewId, request.message());
		return ApiResponseNew.success();
	}

	@PatchMapping("/interviews/{interviewId}/complete")
	public ApiResponseNew<Void> complete(@PathVariable Long interviewId) {
		//Long memberId = SecurityUtil.getCurrentMemberId();
		Long memberId = 3L;
		interviewService.complete(memberId, interviewId);
		return ApiResponseNew.success();
	}
}
