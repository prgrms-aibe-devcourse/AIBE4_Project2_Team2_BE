package kr.java.aibe4_project2_team2_be.majormate.domain.auth.service;

import java.time.LocalDateTime;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.LoginRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.RefreshTokenRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.ResetPasswordRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.request.SignupRequest;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.FindUsernameResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.SignupResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response.TokenResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.EmailVerification.VerificationType;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.RefreshToken;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository.RefreshTokenRepository;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.repository.MemberRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.custom.DuplicateException;
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

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;
	private final EmailService emailService;

	@Transactional
	public SignupResponse signup(SignupRequest request) {
		// 1. 이메일 인증 여부 확인
		if (!emailService.isVerified(request.getEmail(), VerificationType.SIGNUP)) {
			throw new BadRequestException(ErrorCode.EMAIL_NOT_VERIFIED);
		}

		// 2. 아이디 중복 검증
		if (memberRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_USERNAME);
		}

		// 3. 이메일 중복 검증
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 4. 닉네임 중복 검증
		if (memberRepository.existsByNickname(request.getNickname())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
		}

		// 5. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 6. 회원 생성
		Member member = Member.builder()
			.username(request.getUsername())
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.nickname(request.getNickname())
			.status(MemberStatus.ENROLLED)
			.role(MemberRole.STUDENT)
			.build();

		Member savedMember = memberRepository.save(member);

		log.info("회원가입 완료 - ID: {}, Username: {}, Email: {}", savedMember.getMemberId(), savedMember.getUsername(),
			savedMember.getEmail());

		return SignupResponse.from(savedMember);
	}

	/**
	 * 이메일로 아이디 찾기
	 */
	@Transactional
	public FindUsernameResponse findUsername(String email, String code) {
		// 1. 인증 코드 검증
		emailService.verifyCode(email, code, VerificationType.FIND_USERNAME);

		// 2. 이메일로 회원 조회
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		// 3. OAuth2 사용자는 아이디 찾기 불가
		if (member.isOAuth2User()) {
			throw new BadRequestException(ErrorCode.SOCIAL_LOGIN_REQUIRED);
		}

		log.info("아이디 찾기 완료 - Email: {}", email);

		return FindUsernameResponse.of(member.getUsername(), member.getEmail());
	}

	/**
	 * 비밀번호 재설정
	 */
	@Transactional
	public void resetPassword(ResetPasswordRequest request) {
		// 1. 인증 코드 검증
		emailService.verifyCode(request.getEmail(), request.getCode(), VerificationType.RESET_PASSWORD);

		// 2. 이메일로 회원 조회
		Member member = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		// 3. OAuth2 사용자는 비밀번호 재설정 불가
		if (member.isOAuth2User()) {
			throw new BadRequestException(ErrorCode.SOCIAL_LOGIN_REQUIRED);
		}

		// 4. 비밀번호 암호화 및 업데이트
		String encodedPassword = passwordEncoder.encode(request.getNewPassword());
		member.updatePassword(encodedPassword);

		log.info("비밀번호 재설정 완료 - Email: {}", request.getEmail());
	}

	@Transactional
	public TokenResponse login(LoginRequest request) {
		// 1. 사용자 조회
		Member member = memberRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		// 2. OAuth2 사용자 체크
		if (member.isOAuth2User()) {
			throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
		}

		// 3. 비밀번호 검증
		if (member.getPassword() == null ||
			!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
		}

		// 4. 토큰 생성
		String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().name());
		String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

		// 5. RefreshToken 저장
		saveOrUpdateRefreshToken(member.getMemberId(), refreshToken);

		log.info("로그인 성공 - ID: {}, Username: {}", member.getMemberId(), member.getUsername());

		// 6. 토큰 만료 시간 (밀리초 -> 초 변환)
		Long expiresIn = jwtProperties.getAccessTokenValidity() / 1000;

		return TokenResponse.of(accessToken, refreshToken, expiresIn);
	}

	@Transactional
	public TokenResponse refresh(RefreshTokenRequest request) {
		// 1. RefreshToken 조회
		RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
			.orElseThrow(() -> new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

		// 2. 만료 검증
		if (refreshToken.isExpired()) {
			refreshTokenRepository.delete(refreshToken);
			throw new UnauthorizedException(ErrorCode.EXPIRED_TOKEN);
		}

		// 3. 토큰 검증
		if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
			throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
		}

		// 4. 회원 조회
		Member member = memberRepository.findById(refreshToken.getMemberId())
			.orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

		// 5. 새 AccessToken 생성
		String newAccessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRole().name());

		log.info("토큰 갱신 완료 - ID: {}", member.getMemberId());

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
