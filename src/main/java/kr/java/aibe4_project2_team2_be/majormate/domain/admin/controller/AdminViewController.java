package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminMajorRoleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // @RestController가 아니라 @Controller를 써야 합니다!
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final AdminMajorRoleRequestService adminService; // 기존 서비스 재사용

    // 목록 페이지 띄우기
    @GetMapping("/view/requests") // 접속 주소: localhost:8080/admin/view/requests
    public String requestListPage(Model model) {

        // 1. 서비스에서 데이터 가져오기 (메서드 활용)
        // 주의: 현재 서비스 코드가 엔티티 리스트를 반환한다고 가정합니다.
        var requests = adminService.getPendingRequests();

        // 2. HTML로 데이터 넘겨주기
        // "requests"라는 이름으로 HTML 안에서 사용할 수 있게 됩니다.
        model.addAttribute("requests", requests);

        // 3. 보여줄 HTML 파일 위치 지정
        // src/main/resources/admin/request-list.html // thymeleaf 사용 예시 작성중
        return "admin/request-list";
    }
}