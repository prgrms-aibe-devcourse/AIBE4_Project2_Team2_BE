package kr.java.aibe4_project2_team2_be.majormate.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response.MemberResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.service.MemberService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<MemberResponse> getCurrentMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        MemberResponse response = memberService.getCurrentMember(memberId);
        return ApiResponse.success(response, "사용자 정보 조회 성공");
    }
}
