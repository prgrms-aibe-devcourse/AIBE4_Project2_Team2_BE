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

    // 문의 목록 페이지
    @GetMapping
    public String QuestionList (
            Model model,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AdminQuestionDto> questions = adminQnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        return "admin/qna/question-list";
    }

    // 문의 상세 페이지
    @GetMapping("/{id}")
    public String detailQuestion(@PathVariable Long id, Model model) {
        AdminQuestionDetailDto question = adminQnaService.findById(id);
        model.addAttribute("question", question);
        return "admin/qna/question-detail";
    }

    // 문의 삭제 처리
    @PostMapping("/{id}/delete")
    public String deleteQuestion(@PathVariable Long id) {
        adminQnaService.deleteQuestion(id);
        return "redirect:/admin/qna";
    }
}