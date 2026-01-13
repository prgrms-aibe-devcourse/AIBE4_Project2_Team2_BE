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

    // 1-1. 전체 목록 조회
    @GetMapping("/requests/all")
    public String allList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getAllRequests();
        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "ALL");
        return "admin/request-list";
    }

    // 1-2. 신청 대기 목록 조회
    @GetMapping("/requests/pending")
    public String requestList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getPendingRequests();
        model.addAttribute("requests", convertToDtoList(requestEntities));
        model.addAttribute("viewType", "PENDING,RESUBMITTED");
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

    // 1-6. 신청 상세 조회 (디버깅 로그 추가됨)
    @GetMapping("/requests/{request_id}")
    public String requestDetail(@PathVariable("request_id") Long requestId, Model model) {

        System.out.println("==========================================");
        System.out.println(">>> 상세 조회 요청 들어옴. ID: " + requestId);

        try {
            MajorRoleRequest entity = adminService.getRequestDetail(requestId);
            System.out.println(">>> DB 조회 성공: " + entity.getRequestId());

            MajorReqDetailDto dto = new MajorReqDetailDto(entity);
            System.out.println(">>> DTO 변환 성공. 뷰로 이동합니다.");

            model.addAttribute("req", dto);
            return "admin/request-detail";

        } catch (Exception e) {
            // 에러가 나면 콘솔에 빨간색으로 이유를 출력해줍니다.
            System.out.println(">>> 🚨 에러 발생! 원인은 아래와 같습니다:");
            e.printStackTrace();
            throw e; // 브라우저에도 500 에러를 던짐
        }
    }
    // =================== 처리 =================
    // 2. 승인 처리
    @PostMapping("/requests/{id}/accept")
    public String accept(@PathVariable Long id) {
        Long adminId = 1L; // 추후 로그인 세션 적용 필요
        adminService.acceptRequest(id, adminId);

        // [수정] 승인 완료 후 '승인된 목록' 페이지로 이동
        return "redirect:/admin/requests/accepted";
    }
    // 3. 반려 처리
    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/requests/" + id; // 사유 없으면 다시 상세 페이지로
        }
        adminService.rejectRequest(id, adminId, reason);
        // 반려 완료 후 '반려된 목록' 페이지로 이동
        return "redirect:/admin/requests/rejected";
    }

    // 4. 박탈 처리
    @PostMapping("/requests/{id}/revoke")
    public String revoke(@PathVariable Long id, @RequestParam("reason") String reason) {
        Long adminId = 1L;
        adminService.revokeMemberMajorRole(id, adminId, reason);
        return "redirect:/admin/requests/revoked";
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
            this.memberName = entity.getMemberProfile().getNickname();
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
        private LocalDateTime updatedAt;
        private String comment;
        private String documentUrl;
        private String reason;

        public MajorReqDetailDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();

            // [방어 로직] 회원 프로필이 없으면 "알 수 없음" 처리
            if (entity.getMemberProfile() != null) {
                this.memberName = entity.getMemberProfile().getNickname();
            } else {
                this.memberName = "(탈퇴한 회원)";
            }

            this.universityName = entity.getUniversity();
            this.majorName = entity.getMajor();

            // [방어 로직] 상태값이 없으면 에러 방지
            if (entity.getApplicationStatus() != null) {
                this.applicationStatus = entity.getApplicationStatus().getDescription();
            } else {
                this.applicationStatus = "-";
            }

            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getDecidedAt();
            this.comment = entity.getComment();
            this.documentUrl = entity.getDocumentUrl();
            this.reason = entity.getReason();
        }
    }
}