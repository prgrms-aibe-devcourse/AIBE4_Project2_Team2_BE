package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminMajorService;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.RequestStatusHistory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMajorController {

    private final AdminMajorService adminMajorService;

    // Page<Entity> -> Page<DTO> 변환 헬퍼 메서드
    private Page<MajorReqDto> convertToDtoPage(Page<MajorRoleRequest> entities) {
        return entities.map(MajorReqDto::new);
    }

    // 1-1. 전체 목록
    @GetMapping("/requests/all")
    public String allList(Model model,
                          @RequestParam(required = false) String searchType,
                          @RequestParam(required = false) String keyword,
                          @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MajorRoleRequest> requestPage = adminMajorService.getAllRequests(searchType, keyword, pageable);
        model.addAttribute("requests", convertToDtoPage(requestPage));
        model.addAttribute("viewType", "ALL");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-2. 신청 대기 목록
    @GetMapping("/requests/pending")
    public String requestList(Model model,
                              @RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MajorRoleRequest> requestPage = adminMajorService.getPendingRequests(searchType, keyword, pageable);
        model.addAttribute("requests", convertToDtoPage(requestPage));
        model.addAttribute("viewType", "PENDING,RESUBMITTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-3. 승인된 목록
    @GetMapping("/requests/accepted")
    public String acceptedList(Model model,
                               @RequestParam(required = false) String searchType,
                               @RequestParam(required = false) String keyword,
                               @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MajorRoleRequest> requestPage = adminMajorService.getAcceptedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", convertToDtoPage(requestPage));
        model.addAttribute("viewType", "ACCEPTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-4. 반려된 목록
    @GetMapping("/requests/rejected")
    public String rejectedList(Model model,
                               @RequestParam(required = false) String searchType,
                               @RequestParam(required = false) String keyword,
                               @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MajorRoleRequest> requestPage = adminMajorService.getRejectedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", convertToDtoPage(requestPage));
        model.addAttribute("viewType", "REJECTED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-5. 자격 박탈 목록
    @GetMapping("/requests/revoked")
    public String revokedList(Model model,
                              @RequestParam(required = false) String searchType,
                              @RequestParam(required = false) String keyword,
                              @PageableDefault(size = 10, sort = "requestId", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MajorRoleRequest> requestPage = adminMajorService.getRevokedRequests(searchType, keyword, pageable);
        model.addAttribute("requests", convertToDtoPage(requestPage));
        model.addAttribute("viewType", "REVOKED");
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/major/request-list";
    }

    // 1-6. 상세 조회
    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable("id") Long requestId, Model model) {
        MajorRoleRequest entity = adminMajorService.getRequestDetail(requestId);
        model.addAttribute("req", new MajorReqDetailDto(entity));
        return "admin/major/request-detail";
    }

    // 2. 승인 처리
    @PostMapping("/requests/{id}/accept")
    public String accept(@PathVariable Long id) {
        Long adminId = 1L;
        adminMajorService.acceptRequest(id, adminId);
        return "redirect:/admin/requests/accepted";
    }

    // 3. 반려 처리
    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/requests/" + id;
        }
        adminMajorService.rejectRequest(id, adminId, reason);
        return "redirect:/admin/requests/rejected";
    }

    // 4. 박탈 처리
    @PostMapping("/requests/{id}/revoke")
    public String revoke(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        adminMajorService.revokeMemberMajorRole(id, adminId, reason);
        return "redirect:/admin/requests/revoked";
    }

    // --- DTO ---
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
            // null check
            this.memberName = (entity.getMemberProfile() != null) ? entity.getMemberProfile().getNickname() : "(탈퇴)";
            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();
            this.applicationStatus = entity.getApplicationStatus().getDescription();
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
        private LocalDateTime decidedAt;
        private String comment;
        private String documentUrl;
        private String reason;
        private List<MajorHistoryDto> histories;

        public MajorReqDetailDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();
            this.memberName = (entity.getMemberProfile() != null) ? entity.getMemberProfile().getNickname() : "(탈퇴)";
            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();
            this.applicationStatus = entity.getApplicationStatus().getDescription();
            this.createdAt = entity.getCreatedAt();
            this.decidedAt = entity.getDecidedAt();
            this.comment = entity.getComment();
            this.documentUrl = entity.getDocumentUrl();
            this.reason = entity.getReason();
            this.histories = entity.getStatusHistories().stream()
                    .sorted(Comparator.comparing(RequestStatusHistory::getChangedAt).reversed())
                    .map(MajorHistoryDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class MajorHistoryDto {
        private LocalDateTime changedAt;
        private String toStatus;
        private String changedByNickname;
        private String reason;

        public MajorHistoryDto(RequestStatusHistory history) {
            this.changedAt = history.getChangedAt();
            this.toStatus = history.getToStatus().name();
            this.changedByNickname = (history.getChangedBy() != null) ? history.getChangedBy().getNickname() : "(알수없음)";
            this.reason = history.getReason();
        }
    }
}