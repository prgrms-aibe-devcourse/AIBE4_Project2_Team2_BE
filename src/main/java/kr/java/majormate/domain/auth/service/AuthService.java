package kr.java.majormate.domain.auth.service;

import kr.java.majormate.domain.auth.dto.request.LoginRequest;
import kr.java.majormate.domain.auth.dto.request.RefreshTokenRequest;
import kr.java.majormate.domain.auth.dto.request.SignupRequest;
import kr.java.majormate.domain.auth.dto.response.SignupResponse;
import kr.java.majormate.domain.auth.dto.response.TokenResponse;
import kr.java.majormate.domain.auth.entity.RefreshToken;
import kr.java.majormate.domain.auth.repository.RefreshTokenRepository;
import kr.java.majormate.domain.member.entity.Member;
import kr.java.majormate.domain.member.repository.MemberRepository;
import kr.java.majormate.global.common.constant.MemberRole;
import kr.java.majormate.global.common.constant.MemberStatus;
import kr.java.majormate.global.exception.ErrorCode;
import kr.java.majormate.global.exception.custom.DuplicateException;
import kr.java.majormate.global.exception.custom.NotFoundException;
import kr.java.majormate.global.exception.custom.UnauthorizedException;
import kr.java.majormate.global.security.jwt.JwtProperties;
import kr.java.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 이메일 중복 검증
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 2. 닉네임 중복 검증
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 회원 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .nickname(request.getNickname())
                .memberType(request.getMemberType())
                .memberStatus(MemberStatus.ENROLLED)
                .role(MemberRole.STUDENT)
                .build();

        Member savedMember = memberRepository.save(member);

        log.info("회원가입 완료 - ID: {}, Email: {}", savedMember.getId(), savedMember.getEmail());

        return SignupResponse.from(savedMember);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. 사용자 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // 4. RefreshToken 저장
        saveOrUpdateRefreshToken(member.getId(), refreshToken);

        log.info("로그인 성공 - ID: {}, Email: {}", member.getId(), member.getEmail());

        // 5. 토큰 만료 시간 (밀리초 -> 초 변환)
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
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name());

        log.info("토큰 갱신 완료 - ID: {}", member.getId());

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
