package kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.entity.RefreshToken;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.repository.RefreshTokenRepository;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtProperties;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to {}", redirectUri);
            return;
        }

        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long memberId = oauth2User.getMemberId();
        String role = oauth2User.getRole();

        // Generate JWT tokens
        String accessToken = jwtTokenProvider.createAccessToken(memberId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        // Save refresh token
        saveOrUpdateRefreshToken(memberId, refreshToken);

        log.info("OAuth2 login success - Member ID: {}, Role: {}", memberId, role);

        // Redirect to frontend with tokens
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("tokenType", "Bearer")
                .queryParam("expiresIn", jwtProperties.getAccessTokenValidity() / 1000)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
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
