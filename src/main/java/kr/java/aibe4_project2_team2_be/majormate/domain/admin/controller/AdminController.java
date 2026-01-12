package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminService;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    // 1-1 전체 목록 조회
    @GetMapping("/requests/all")
    public String allList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getAllRequests();

        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "ALL");
        return "admin/request-list";
    }

    // 1-2. 신청 대기 목록 조회
    @GetMapping("/requests")
    public String requestList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getPendingRequests();

        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "PENDING"); // 현재 보고 있는 탭 정보
        return "admin/request-list";
    }

    // 1-3. 승인된 목록 조회
    @GetMapping("/requests/accepted")
    public String acceptedList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getAcceptedRequests();

        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "ACCEPTED");
        return "admin/request-list";
    }

    // 1-4. 반려된 목록 조회
    @GetMapping("/requests/rejected")
    public String rejectedList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getRejectedRequests();

        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "REJECTED");
        return "admin/request-list";
    }

    // 1-5. 자격 박탈 목록 조회
    @GetMapping("/requests/revoked")
    public String revokedList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getRevokedRequests();

        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "REVOKED");
        return "admin/request-list";
    }

    // 1-6. 신청 상세 조회
    @GetMapping("/requests/{request_id}")
    public String requestDetail(@PathVariable Long request_id, Model model) {
        MajorRoleRequest entity = adminService.getRequestDetail(request_id);
        MajorReqDetailDto dto = new MajorReqDetailDto(entity);
        model.addAttribute("req", dto);
        return "admin/request-detail";
    }

    // 2. 승인 처리
    @PostMapping("/requests/{id}/accept")
    public String accept(@PathVariable Long id) {
        Long adminId = 1L; // 추후 로그인 세션에서 가져오도록 수정 필요
        adminService.acceptRequest(id, adminId);
        return "redirect:/admin/requests"; // 처리 후 대기 목록으로 이동
    }

    // 3. 반려 처리
    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/requests/" + id;
        }
        adminService.rejectRequest(id, adminId, reason);
        return "redirect:/admin/requests";
    }

    // 4. 박탈 처리
    @PostMapping("/requests/{id}/revoke")
    public String revoke(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        adminService.revokeMemberMajorRole(id, adminId, reason);
        // 박탈은 주로 '승인된 목록'에서 수행하므로 처리 후 승인 목록이나 박탈 목록으로 이동
        return "redirect:/admin/requests/accepted";
    }

    // Dto 변환 로직
    private List<MajorReqDto> convertToDtoList(List<MajorRoleRequest> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(MajorRoleRequest::getRequestId).reversed())
                .map(MajorReqDto::new)
                .collect(Collectors.toList());
    }

    // DTO 클래스들
    @Getter
    static class MajorReqDto {
        private Long id;
        private String memberName;
        private String universityName;
        private String majorName;
        private String applicationStatus;
        private LocalDateTime createdAt;

        public MajorReqDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();
            this.memberName = entity.getNickname();
            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();
            this.applicationStatus = String.valueOf(entity.getApplicationStatus());
            this.createdAt = entity.getCreatedAt();
        }
    }

    @Getter
    static class MajorReqDetailDto {
        private Long id;
        private String memberName;
        private String universityName;
        private String majorName;
        private String applicationStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String comment;
        private String documentUrl;
        private String reason;

        public MajorReqDetailDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();
            this.memberName = entity.getNickname();
            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();
            this.applicationStatus = String.valueOf(entity.getApplicationStatus().getDescription());
            // ApplicationStatus의 Status 한글 사용
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getDecidedAt();
            this.comment = entity.getComment();
            this.documentUrl = entity.getDocumentUrl();
            this.reason = entity.getReason();
        }
    }
}