package kr.java.aibe4_project2_team2_be.majormate.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.CheckProviderRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.FindUsernameRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.LoginRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.RefreshTokenRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.ResetPasswordRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.SendVerificationCodeRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.SignupRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.VerifyCodeRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.CheckProviderResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.FindUsernameResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.SignupResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.TokenResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.service.AuthService;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.service.EmailService;
import kr.java.aibe4_project2_team2_be.majormate.global.common.response.ApiResponse;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.UnauthorizedException;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtProperties;
import kr.java.aibe4_project2_team2_be.majormate.global.util.CookieUtil;
import kr.java.aibe4_project2_team2_be.majormate.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final EmailService emailService;
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
		int cookieMaxAge = (int)(jwtProperties.getRefreshTokenValidity() / 1000);
		CookieUtil.addCookie(response, "refreshToken", tokenResponse.getRefreshToken(), cookieMaxAge);

		// Set access token as HttpOnly cookie
		int accessCookieMaxAge = tokenResponse.getExpiresIn().intValue();
		CookieUtil.addCookie(response, "accessToken", tokenResponse.getAccessToken(), accessCookieMaxAge);

		// Return response without tokens in body (tokens are in HttpOnly cookies for security)
		TokenResponse responseWithoutTokens = TokenResponse.builder()
			.tokenType(tokenResponse.getTokenType())
			.expiresIn(tokenResponse.getExpiresIn())
			.build();

		return ApiResponse.success(responseWithoutTokens, "로그인 성공");
	}

	@Operation(summary = "토큰 갱신", description = "쿠키의 RefreshToken으로 AccessToken을 갱신합니다.")
	@PostMapping("/refresh")
	public ApiResponse<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
		// Get refresh token from cookie
		Cookie refreshTokenCookie = CookieUtil.getCookie(request, "refreshToken")
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_401_REFRESH_TOKEN_NOT_FOUND));

		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshTokenCookie.getValue());
		TokenResponse tokenResponse = authService.refresh(refreshTokenRequest);

		// Set new access token as HttpOnly cookie
		int accessCookieMaxAge = tokenResponse.getExpiresIn().intValue();
		CookieUtil.addCookie(response, "accessToken", tokenResponse.getAccessToken(), accessCookieMaxAge);

		// Return response without tokens in body (tokens are in HttpOnly cookies for security)
		TokenResponse responseWithoutTokens = TokenResponse.builder()
			.tokenType(tokenResponse.getTokenType())
			.expiresIn(tokenResponse.getExpiresIn())
			.build();

		return ApiResponse.success(responseWithoutTokens, "토큰 갱신 완료");
	}

	@Operation(summary = "로그아웃", description = "로그아웃하고 RefreshToken을 무효화합니다.")
	@PostMapping("/logout")
	public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		authService.logout(memberId);

		// Delete refresh token cookie
		CookieUtil.deleteCookie(request, response, "refreshToken");
		//엑세스토큰 쿠키도 같이 삭제하도록 추가하였습니다 ( 조현우 )
		CookieUtil.deleteCookie(request, response, "accessToken");

		return ApiResponse.success("로그아웃 되었습니다.");
	}

	// ========== 이메일 인증 API ==========

	@Operation(summary = "이메일 인증 코드 발송", description = "회원가입, 아이디 찾기, 비밀번호 재설정 시 이메일 인증 코드를 발송합니다. 요청 DTO에 type (SIGNUP, FIND_USERNAME, RESET_PASSWORD)을 포함해야 합니다.")
	@PostMapping("/email/send")
	public ApiResponse<Void> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
		emailService.sendVerificationCode(request.getEmail(), request.getType());
		return ApiResponse.success("인증 코드가 발송되었습니다.");
	}

	@Operation(summary = "이메일 인증 코드 검증", description = "발송된 이메일 인증 코드를 검증합니다. 요청 DTO에 type (SIGNUP, FIND_USERNAME, RESET_PASSWORD)을 포함해야 합니다.")
	@PostMapping("/email/verify")
	public ApiResponse<Void> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
		emailService.verifyCode(request.getEmail(), request.getCode(), request.getType());
		return ApiResponse.success("이메일 인증이 완료되었습니다.");
	}

	@Operation(summary = "아이디 찾기", description = "이메일 인증 후 아이디를 조회합니다.")
	@PostMapping("/find-username")
	public ApiResponse<FindUsernameResponse> findUsername(@Valid @RequestBody FindUsernameRequest request) {
		FindUsernameResponse response = authService.findUsername(request.getEmail());
		return ApiResponse.success(response, "아이디 찾기가 완료되었습니다.");
	}

	@Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 비밀번호를 재설정합니다.")
	@PostMapping("/reset-password")
	public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		authService.resetPassword(request);
		return ApiResponse.success("비밀번호가 재설정되었습니다.");
	}

	@Operation(summary = "계정 타입 확인", description = "아이디와 이메일로 계정의 로그인 방식(소셜/일반)을 확인합니다.")
	@PostMapping("/check-provider")
	public ApiResponse<CheckProviderResponse> checkProvider(@Valid @RequestBody CheckProviderRequest request) {
		CheckProviderResponse response = authService.checkProvider(request);
		return ApiResponse.success(response, "계정 타입 확인이 완료되었습니다.");
	}
}
