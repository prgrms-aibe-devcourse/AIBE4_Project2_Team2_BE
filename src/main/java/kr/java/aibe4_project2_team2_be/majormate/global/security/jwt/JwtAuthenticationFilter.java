package kr.java.aibe4_project2_team2_be.majormate.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie; // 추가됨
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays; // 추가됨

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 토큰 추출 (헤더 혹은 쿠키)
        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI();

        // 2. 토큰 유효성 검사 및 인증 처리
        if (StringUtils.hasText(jwt)) {
            try {
                if (jwtTokenProvider.validateToken(jwt)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // SSE 연결 요청일 때만 로그를 INFO로 찍어서 확인 (너무 시끄러우니까)
                    if (requestURI.startsWith("/api/notifications")) {
                        log.info("✅ [Filter] SSE 연결 인증 성공! User: {}", authentication.getName());
                    } else {
                        log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
                    }
                } else {
                    if (requestURI.startsWith("/api/notifications")) {
                        log.warn("❌ [Filter] SSE 연결 실패: 토큰이 유효하지 않습니다.");
                    }
                }
            } catch (Exception e) {
                log.error("JWT 처리 중 오류 발생: {}", e.getMessage());
            }
        } else {
            // 토큰이 없는 경우 (SSE 요청인데 토큰 없으면 경고 로그)
            if (requestURI.startsWith("/api/notifications")) {
                log.warn("⚠️ [Filter] SSE 요청인데 토큰을 찾을 수 없습니다. (헤더/쿠키 확인 필요)");
            }
        }

        filterChain.doFilter(request, response);
    }

    // ★★★ 여기가 가장 중요하게 수정된 부분입니다 ★★★
    private String resolveToken(HttpServletRequest request) {
        // 1. 먼저 Authorization 헤더 확인 (기존 로직)
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        // 2. 헤더에 없으면 쿠키 확인 (SSE 연결용 추가 로직)
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "accessToken".equals(c.getName())) // 쿠키 이름이 "accessToken"인 것을 찾음
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")
                || path.equals("/error");
    }
}