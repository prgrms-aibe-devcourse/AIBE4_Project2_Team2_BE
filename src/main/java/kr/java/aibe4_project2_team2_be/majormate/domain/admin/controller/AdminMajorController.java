package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminMajorReqDetailDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.dto.response.AdminMajorReqDto;
import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminMajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMajorController {

    private final AdminMajorService adminMajorService;

    // 1-1. 전체 목록
    @GetMapping("/major/requests/all")
    public String MajorAllList(Model model,
                          @RequestParam(required = false) String searchType,
                          @RequestParam(required = false) String keyword,
                          @PageableDefault(sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminMajorReqDto> requestPage = adminMajorService.getAllRequests(searchType, keyword, pageable);

        model.addAttribute("requests", requestPage);
        model.addAttribute("viewType", "ALL");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-2. 신청 대기 목록
    @GetMapping("/major/requests/pending")
    public String MajorRequestList(Model model,
                              @RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminMajorReqDto> requestPage = adminMajorService.getPendingRequests(searchType, keyword, pageable);
        model.addAttribute("requests", requestPage);
        model.addAttribute("viewType", "PENDING,RESUBMITTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-3. 승인된 목록
    @GetMapping("/major/requests/accepted")
    public String MajorAcceptedList(Model model,
                               @RequestParam(required = false) String searchType,
                               @RequestParam(required = false) String keyword,
                               @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminMajorReqDto> requestPage = adminMajorService.getAcceptedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", requestPage);
        model.addAttribute("viewType", "ACCEPTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-4. 반려된 목록
    @GetMapping("/major/requests/rejected")
    public String MajorRejectedList(Model model,
                               @RequestParam(required = false) String searchType,
                               @RequestParam(required = false) String keyword,
                               @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminMajorReqDto> requestPage = adminMajorService.getRejectedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", requestPage);
        model.addAttribute("viewType", "REJECTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-5. 자격 박탈 목록
    @GetMapping("/major/requests/revoked")
    public String MajorRevokedList(Model model,
                              @RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AdminMajorReqDto> requestPage = adminMajorService.getRevokedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", requestPage);
        model.addAttribute("viewType", "REVOKED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-6. 상세 조회
    @GetMapping("/major/requests/{id}")
    public String MajorRequestDetail(@PathVariable("id") Long requestId, Model model) {
        // Service가 이미 DetailDto를 줍니다.
        AdminMajorReqDetailDto detailDto = adminMajorService.getRequestDetail(requestId);
        model.addAttribute("req", detailDto);
        return "admin/major/request-detail";
    }

    // 2. 승인 처리
    @PostMapping("/major/requests/{id}/accept")
    public String MajorAccept(@PathVariable Long id) {
        Long adminId = 1L;
        adminMajorService.acceptRequest(id, adminId);
        return "redirect:/admin/major/requests/accepted";
    }

    // 3. 반려 처리
    @PostMapping("/major/requests/{id}/reject")
    public String MajorReject(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/requests/" + id;
        }
        adminMajorService.rejectRequest(id, adminId, reason);
        return "redirect:/admin/major/requests/rejected";
    }

    // 4. 박탈 처리
    @PostMapping("/major/requests/{id}/revoke")
    public String MajorRevoke(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        adminMajorService.revokeMemberMajorRole(id, adminId, reason);
        return "redirect:/admin/major/requests/revoked";
    }
}