package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    // 1. 관리자 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "admin/main/login";
    }

    // 2. 관리자 메인 대시보드 (로그인 성공 후 이동)
    @GetMapping("/main")
    public String mainPage() {
        return "admin/main/main";
    }
}