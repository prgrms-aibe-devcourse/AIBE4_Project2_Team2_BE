package kr.java.aibe4_project2_team2_be.majormate.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.LoginRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.RefreshTokenRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.SignupRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.SignupResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.TokenResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.service.AuthService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.UnauthorizedException;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtProperties;
import kr.java.aibe4_project2_team2_be.majormate.global.util.CookieUtil;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(response, "회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);

        // Set refresh token as HttpOnly cookie
        int cookieMaxAge = (int) (jwtProperties.getRefreshTokenValidity() / 1000);
        CookieUtil.addCookie(response, "refreshToken", tokenResponse.getRefreshToken(), cookieMaxAge);

        // Return response with access token only (no refresh token in body)
        TokenResponse responseWithoutRefreshToken = TokenResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .build();

        return ApiResponse.success(responseWithoutRefreshToken, "로그인 성공");
    }

    @Operation(summary = "토큰 갱신", description = "쿠키의 RefreshToken으로 AccessToken을 갱신합니다.")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(HttpServletRequest request) {
        // Get refresh token from cookie
        Cookie refreshTokenCookie = CookieUtil.getCookie(request, "refreshToken")
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshTokenCookie.getValue());
        TokenResponse response = authService.refresh(refreshTokenRequest);
        return ApiResponse.success(response, "토큰 갱신 완료");
    }

    @Operation(summary = "로그아웃", description = "로그아웃하고 RefreshToken을 무효화합니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        authService.logout(memberId);

        // Delete refresh token cookie
        CookieUtil.deleteCookie(request, response, "refreshToken");

        return ApiResponse.success("로그아웃 되었습니다.");
    }
}
