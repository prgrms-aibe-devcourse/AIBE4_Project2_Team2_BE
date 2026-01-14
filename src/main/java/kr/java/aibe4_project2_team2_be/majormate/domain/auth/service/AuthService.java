package kr.java.aibe4_project2_team2_be.majormate.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.CheckProviderRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.LoginRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.RefreshTokenRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.ResetPasswordRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.SignupRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.CheckProviderResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.FindUsernameResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.SignupResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.TokenResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification.VerificationType;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.RefreshToken;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository.RefreshTokenRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberProfileRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.NotFoundException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.UnauthorizedException;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtProperties;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final MemberProfileRepository memberProfileRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;
	private final EmailService emailService;

	@Transactional
	public SignupResponse signup(SignupRequest request) {
		// 1. 이메일 인증 여부 확인
		if (!emailService.isVerified(request.getEmail(), VerificationType.SIGNUP)) {
			throw new BadRequestException(ErrorCode.AUTH_400_EMAIL_NOT_VERIFIED);
		}

		// 2. 아이디 중복 검증
		if (memberProfileRepository.existsByUsername(request.getUsername())) {
			throw new BusinessException(ErrorCode.MEMBER_409_DUPLICATE_USERNAME);
		}

		// 3. 이메일 중복 검증
		if (memberProfileRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(ErrorCode.MEMBER_409_DUPLICATE_EMAIL);
		}

		// 4. 닉네임 중복 검증
		if (memberProfileRepository.existsByNickname(request.getNickname())) {
			throw new BusinessException(ErrorCode.MEMBER_409_DUPLICATE_NICKNAME);
		}

		// 5. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 5. 회원 생성
		MemberProfile memberProfile = MemberProfile.createLocal(
			request.getName(), request.getNickname(), request.getEmail(), request.getUsername(), encodedPassword
		);

		MemberProfile savedMemberProfile = memberProfileRepository.save(memberProfile);

		log.info("회원가입 완료 - ID: {}, Username: {}, Email: {}", savedMemberProfile.getMemberId(),
			savedMemberProfile.getUsername(),
			savedMemberProfile.getEmail());

		return SignupResponse.from(savedMemberProfile);
	}

	/**
	 * 이메일로 아이디 찾기
	 */
	@Transactional
	public FindUsernameResponse findUsername(String email) {
		// 1. 이메일 인증 여부 확인
		if (!emailService.isVerified(email, VerificationType.FIND_USERNAME)) {
			throw new BadRequestException(ErrorCode.AUTH_400_EMAIL_NOT_VERIFIED);
		}

		// 2. 이메일로 회원 조회
		MemberProfile memberProfile = memberProfileRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_404));

		// 3. 소셜/일반 유저 분기 처리
		if (memberProfile.isOAuth2User()) {
			// 소셜 로그인 사용자인 경우, 가입된 소셜 서비스 정보와 함께 응답
			String provider = memberProfile.getSocialAccounts().get(0).getAuthProvider().name();
			log.info("아이디 찾기 시도 (소셜 계정) - Email: {}, Provider: {}", email, provider);
			return FindUsernameResponse.builder()
				.username(null)
				.provider(provider)
				.build();
		} else {
			// 일반 로그인 사용자인 경우, 아이디와 "LOCAL" 프로바이더 정보 응답
			log.info("아이디 찾기 완료 - Email: {}", email);
			return FindUsernameResponse.builder()
				.username(memberProfile.getUsername())
				.provider("LOCAL")
				.build();
		}
	}

	/**
	 * 비밀번호 재설정
	 */
	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		// 1. 이메일 인증 여부 확인
		if (!emailService.isVerified(request.getEmail(), VerificationType.RESET_PASSWORD)) {
			throw new BadRequestException(ErrorCode.AUTH_400_EMAIL_NOT_VERIFIED);
		}

		// 2. 이메일로 회원 조회
		MemberProfile memberProfile = memberProfileRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_404));

		// 3. OAuth2 사용자는 비밀번호 재설정 불가
		if (memberProfile.isOAuth2User()) {
			throw new BadRequestException(ErrorCode.AUTH_400_SOCIAL_LOGIN_REQUIRED);
		}

		// 4. 비밀번호 암호화 및 업데이트
		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		memberProfile.updatePassword(encodedPassword);

		log.info("비밀번호 재설정 완료 - Email: {}", request.getEmail());
	}

	/**
	 * 계정 타입 확인 (소셜/일반 로그인 구분)
	 */
	@Transactional(readOnly = true)
	public CheckProviderResponse checkProvider(CheckProviderRequest request) {
		// 1. 이메일로 회원 조회
		MemberProfile memberProfile = memberProfileRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_404));

		// 2. 소셜/일반 유저 분기 처리
		if (memberProfile.isOAuth2User()) {
			// 소셜 로그인 사용자인 경우
			String provider = memberProfile.getSocialAccounts().get(0).getAuthProvider().name();
			log.info("계정 타입 확인 (소셜 계정) - Email: {}, Provider: {}",
				request.getEmail(), provider);
			return CheckProviderResponse.builder()
				.provider(provider)
				.build();
		} else {
			// 일반 로그인 사용자인 경우
			log.info("계정 타입 확인 (일반 계정) - Email: {}",
				request.getEmail());
			return CheckProviderResponse.builder()
				.provider("LOCAL")
				.build();
		}
	}

	@Transactional
	public TokenResponse login(LoginRequest request) {
		// 1. 사용자 조회
		MemberProfile memberProfile = memberProfileRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_404));

		// 2. OAuth2 사용자 체크
		if (memberProfile.isOAuth2User()) {
			throw new BadRequestException(ErrorCode.AUTH_400_SOCIAL_LOGIN_REQUIRED);
		}

		// 3. 비밀번호 검증
		if (memberProfile.getPassword() == null ||
			!passwordEncoder.matches(request.getPassword(), memberProfile.getPassword())) {
			throw new UnauthorizedException(ErrorCode.AUTH_401_INVALID_PASSWORD);
		}

		// 4. 토큰 생성
		String accessToken = jwtTokenProvider.createAccessToken(memberProfile.getMemberId(),
			memberProfile.getRole().name());
		String refreshToken = jwtTokenProvider.createRefreshToken(memberProfile.getMemberId());

		// 5. RefreshToken 저장
		saveOrUpdateRefreshToken(memberProfile.getMemberId(), refreshToken);

		log.info("로그인 성공 - ID: {}, Username: {}", memberProfile.getMemberId(), memberProfile.getUsername());

		// 6. 토큰 만료 시간 (밀리초 -> 초 변환)
		Long expiresIn = jwtProperties.getAccessTokenValidity() / 1000;

		return TokenResponse.of(accessToken, refreshToken, expiresIn);
	}

	@Transactional
	public TokenResponse refresh(RefreshTokenRequest request) {
		// 1. RefreshToken 조회
		RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_401_REFRESH_TOKEN_NOT_FOUND));

		// 2. 만료 검증
		if (refreshToken.isExpired()) {
			refreshTokenRepository.delete(refreshToken);
			throw new UnauthorizedException(ErrorCode.AUTH_401_EXPIRED_TOKEN);
		}

		// 3. 토큰 검증
		if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
			throw new UnauthorizedException(ErrorCode.AUTH_401_INVALID_TOKEN);
		}

		// 4. 회원 조회
		MemberProfile memberProfile = memberProfileRepository.findById(refreshToken.getMemberId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_404));

		// 5. 새 AccessToken 생성
		String newAccessToken = jwtTokenProvider.createAccessToken(memberProfile.getMemberId(),
			memberProfile.getRole().name());

		log.info("토큰 갱신 완료 - ID: {}", memberProfile.getMemberId());

		Long expiresIn = jwtProperties.getAccessTokenValidity() / 1000;

		return TokenResponse.of(newAccessToken, request.getRefreshToken(), expiresIn);
	}

	@Transactional
	public void logout(Long memberId) {
		refreshTokenRepository.deleteByMemberId(memberId);
		log.info("로그아웃 완료 - ID: {}", memberId);
	}

	private void saveOrUpdateRefreshToken(Long memberId, String token) {
		LocalDateTime expiresAt = LocalDateTime.now()
			.plusSeconds(jwtProperties.getRefreshTokenValidity() / 1000);

		refreshTokenRepository.findByMemberId(memberId)
			.ifPresentOrElse(
				existingToken -> existingToken.updateToken(token, expiresAt),
				() -> {
					RefreshToken newToken = RefreshToken.builder()
						.memberId(memberId)
						.token(token)
						.expiresAt(expiresAt)
						.build();
					refreshTokenRepository.save(newToken);
				}
			);
	}
}
