package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminQuestionDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminQnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/qna")
@RequiredArgsConstructor
public class AdminQnaController {

    private final AdminQnaService adminQnaService;

    // 1. 목록 조회 (qna의 메인페이지)
    @GetMapping
    public String questionList(Model model,
                               @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminQuestionDto> questions = adminQnaService.findAllQuestions(pageable);
        model.addAttribute("questions", questions);
        return "admin/qna/question-list";
    }

    // 2. 상세 조회
    @GetMapping("/{id}")
    public String questionDetail(@PathVariable Long id, Model model) {
        AdminQuestionDetailDto question = adminQnaService.findQuestionDetail(id);
        model.addAttribute("question", question);
        return "admin/qna/question-detail";
    }

    // ===========================
    // 3. 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id) {
        adminQnaService.deleteQuestion(id);
        return "redirect:/admin/qna";
    }
}