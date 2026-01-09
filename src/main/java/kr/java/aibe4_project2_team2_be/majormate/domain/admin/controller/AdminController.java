package kr.java.aibe4_project2_team2_be.majormate.domain.admin.controller;

import kr.java.aibe4_project2_team2_be.majormate.domain.admin.service.AdminService;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.entity.MajorRoleRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/requests")
    public String requestList(Model model) {
        List<MajorRoleRequest> requestEntities = adminService.getPendingRequests();

        // Entity -> DTO 변환
        List<MajorReqDto> dtoList = requestEntities.stream()
                .map(MajorReqDto::new)
                .collect(Collectors.toList());

        model.addAttribute("requests", dtoList);
        return "admin/request-list";
    }


    @Getter
    static class MajorReqDto {
        private Long id;
        private String memberName;      // DTO에서는 memberName이라고 이름 지음
        private String universityName;
        private String majorName;
        private String applicationStatus;
        private LocalDateTime createdAt;

        public MajorReqDto(MajorRoleRequest entity) {
            this.id = entity.getRequestId();  // [중요] getId() -> getRequestId()
            this.memberName = entity.getNickname(); // [중요] memberProfile 대신 nickname 사용
            this.universityName = entity.getUniversity(); // [중요] getUniversityName() -> getUniversity()
            this.majorName = entity.getMajor(); // [중요] getMajorName() -> getMajor()
            this.applicationStatus = String.valueOf(entity.getApplicationStatus());
            this.createdAt = entity.getCreatedAt();
        }
    }
}