package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminInterviewDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminInterviewDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/interviews")
@RequiredArgsConstructor
public class AdminInterviewController {

    private final AdminInterviewService adminInterviewService;

    // 1. 인터뷰 목록 페이지
    @GetMapping
    public String interviewList(Model model,
                                @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminInterviewDto> interviews = adminInterviewService.findAllInterviews(pageable);
        model.addAttribute("interviews", interviews);
        return "admin/interview/interview-list";
    }

    // 2. 인터뷰 상세 페이지
    @GetMapping("/{interviewId}")
    public String interviewDetail(@PathVariable Long interviewId, Model model) {
        AdminInterviewDetailDto interview = adminInterviewService.findInterviewDetail(interviewId);
        model.addAttribute("interview", interview);
        return "admin/interview/interview-detail";
    }

    // 3. 인터뷰 삭제 처리
    @PostMapping("/{interviewId}/delete")
    public String deleteInterview(@PathVariable Long interviewId) {
        adminInterviewService.deleteInterview(interviewId);
        return "redirect:/admin/interviews";
    }
}