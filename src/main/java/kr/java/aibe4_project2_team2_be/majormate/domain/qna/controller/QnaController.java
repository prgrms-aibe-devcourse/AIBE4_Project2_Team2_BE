package kr.java.aibe4_project2_team2_be.majormate.domain.qna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.request.QnaRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.IdResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.MyAnswerItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QuestionMyItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.dto.response.QuestionReceivedItemResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.qna.service.QnaService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QnaController {

	private final QnaService qnaService;

	@GetMapping("/members/me/questions")
	public ApiResponseNew<List<QuestionMyItemResponse>> getMyQuestions(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<QuestionMyItemResponse> page = qnaService.getMyQuestions(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/received-questions")
	public ApiResponseNew<List<QuestionReceivedItemResponse>> getReceivedQuestions(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<QuestionReceivedItemResponse> page = qnaService.getReceivedQuestions(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/{memberId}/questions/received")
	public ApiResponseNew<java.util.List<QuestionReceivedItemResponse>> getQuestionsAndAnswer(@PathVariable Long memberId ,Pageable pageable) {
		Page<QuestionReceivedItemResponse> page = qnaService.getReceivedQuestions(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@GetMapping("/members/me/answers")
	public ApiResponseNew<java.util.List<MyAnswerItemResponse>> getMyAnswers(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<MyAnswerItemResponse> page = qnaService.getMyAnswers(memberId, pageable);
		return PageResponsesNew.of(page);
	}

	@PostMapping("/majors/{majorId}/questions")
	public ApiResponseNew<IdResponse> createQuestion(
		@PathVariable Long majorId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		IdResponse response = qnaService.createQuestion(memberId, majorId, request);
		return ApiResponseNew.success(response);
	}

	@PostMapping("/questions/{questionId}/answer")
	public ApiResponseNew<IdResponse> createAnswer(
		@PathVariable Long questionId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		IdResponse response = qnaService.createAnswer(memberId, questionId, request);
		return ApiResponseNew.success(response);
	}

	@PatchMapping("/answers/{answerId}")
	public ApiResponseNew<Void> updateMyAnswer(
		@PathVariable Long answerId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.updateMyAnswer(memberId, answerId, request);
		return ApiResponseNew.success();
	}

	@PatchMapping("/questions/{questionId}")
	public ApiResponseNew<Void> updateMyQuestion(
		@PathVariable Long questionId,
		@Valid @RequestBody QnaRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.updateMyQuestion(memberId, questionId, request);
		return ApiResponseNew.success();
	}

	@DeleteMapping("/questions/{questionId}")
	public ApiResponseNew<Void> deleteMyQuestion(@PathVariable Long questionId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		qnaService.deleteMyQuestion(memberId, questionId);
		return ApiResponseNew.success();
	}
}
