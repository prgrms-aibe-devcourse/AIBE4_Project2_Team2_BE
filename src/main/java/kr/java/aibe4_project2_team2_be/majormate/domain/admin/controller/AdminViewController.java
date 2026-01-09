// AdminViewController
package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminService;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final AdminService adminService;

    // 전공자 신청 목록 페이지
    @GetMapping("/requests")
    public String requestListPage(Model model) {
        // Service가 이제 List<MajorRoleRequest> (Entity 리스트)를 반환합니다.
        List<MajorRoleRequest> requests = adminService.getPendingRequests();

        // Model에 담습니다.
        model.addAttribute("requests", requests);

        return "admin/request-list.html";
    }

    // 상세 페이지
    @GetMapping("/request/{requestId}")
    public String requestDetailPage(@PathVariable Long requestId, Model model) {
        // 1. 서비스에서 데이터 가져오기
        MajorRoleRequest request = adminService.getRequestDetail(requestId);

        // 2. 모델에 담기
        model.addAttribute("request", request);

        // 3. HTML 파일 위치 알려주기 (templates/admin/request-detail.html)
        return "admin/request-detail";
    }
}
