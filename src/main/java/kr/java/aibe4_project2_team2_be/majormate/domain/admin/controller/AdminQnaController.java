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

    // 목록 페이지
    @GetMapping
    public String list(
            Model model,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<AdminQuestionDto> questions = adminQnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        return "admin/qna/question-list";
    }

    // 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        AdminQuestionDetailDto question = adminQnaService.findById(id);
        model.addAttribute("question", question);
        return "admin/qna/question-detail";
    }

    // 답변 등록 처리
    @PostMapping("/{id}/answer")
    public String createAnswer(
            @PathVariable Long id,
            @RequestParam("content") String content
    ) {
        // 관리자가 강제로 답변을 다는 기능
        adminQnaService.createAnswer(id, content);
        return "redirect:/admin/qna/" + id;
    }
}