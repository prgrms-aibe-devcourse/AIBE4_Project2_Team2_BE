package kr.java.aibe4_project2_team2_be.majormate.domain.qna.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.java.aibe4_project2_team2_be.majormate.domain.qna.service.QnaService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.ApiResponseNew;
import kr.java.aibe4_project2_team2_be.majormate.global.common.responsenew.PageResponsesNew;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestionController {

	private final QnaService qnaService;

	@GetMapping("/members/me/questions")
	public ApiResponseNew<List<QuestionAnswerResponse>> getMyQuestions(Pageable pageable) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		Page<QuestionAnswerResponse> page = qnaService.getQuestions(memberId, pageable);
		return PageResponsesNew.of(page);
	}

}
