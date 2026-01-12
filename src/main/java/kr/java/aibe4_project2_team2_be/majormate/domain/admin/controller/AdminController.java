package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminService;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator; // [필수] 정렬을 위해 추가
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 목록 조회
    @GetMapping("/requests")
    public String requestList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getPendingRequests();

        // Entity -> DTO 변환 과정에서 정렬 추가
        List<MajorReqDto> dtoList = requestEntities.stream()
                // 기준으로 역순(최신순) 정렬
                .sorted(Comparator.comparing(MajorRoleRequest::getRequestId).reversed())
                .map(MajorReqDto::new)
                .collect(Collectors.toList());

        model.addAttribute("requests", dtoList);
        return "admin/request-list";
    }

    // 상세 조회
    @GetMapping("/requests/{request_id}")
    public String requestDetail(@PathVariable Long request_id, Model model) {
        MajorRoleRequest entity = adminService.getRequestDetail(request_id);
        MajorReqDetailDto dto = new MajorReqDetailDto(entity);
        model.addAttribute("req", dto);
        return "admin/request-detail";
    }


    // 승인 처리
    @PostMapping("/requests/{id}/accept")
    public String accept(@PathVariable Long id) {
        // 현재 로그인한 관리자 ID (나중에는 세션에서 가져와야 함)
        Long adminId = 1L;

        adminService.acceptRequest(id, adminId);

        // 처리가 끝나면 다시 목록 페이지로 이동
        return "redirect:/admin/requests";
    }

    // 반려 처리
    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;

        // 반려 사유가 비어있으면 안 되므로 간단한 방어 로직 (선택 사항)
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/requests/" + id; // 다시 상세 페이지로
        }

        adminService.rejectRequest(id, adminId, reason);

        return "redirect:/admin/requests";
    }
    
    // DTO 클래스들
    @Getter
    static class MajorReqDto { // 목록 조회
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
    static class MajorReqDetailDto { // 상세 조회
        private Long id;
        private String memberName;
        private String universityName;
        private String majorName;
        private String applicationStatus;
        private LocalDateTime createdAt;
        private String comment;
        private String documentUrl;

        public MajorReqDetailDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();
            this.memberName = entity.getNickname();
            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();
            this.applicationStatus = String.valueOf(entity.getApplicationStatus());
            this.createdAt = entity.getCreatedAt();
            this.comment = entity.getComment();
            this.documentUrl = entity.getDocumentUrl();
        }
    }
}