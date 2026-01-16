package kr.java.aibe4_project2_team2_be.majormate.global.config;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.CustomOAuth2UserService;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationFailureHandler;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2FailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // [1] 관리자 페이지용 설정 (우선순위 1)
    // - 세션 사용 (로그인 유지)
    // - Form 로그인 사용
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**") // /admin으로 시작하는 URL만 이 설정을 따름
                .csrf(AbstractHttpConfigurer::disable) // 개발 편의상 off
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/login-proc", "/css/**", "/js/**", "/images/**").permitAll() // 로그인 화면은 누구나 접근
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 그 외 관리자 페이지는 ADMIN 권한 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")        // 컨트롤러 URL
                        .loginProcessingUrl("/admin/login-proc") // HTML Form Action URL
                        .defaultSuccessUrl("/admin/main", true)  // 로그인 성공 시 이동
                        .failureUrl("/admin/login?error=true")   // 실패 시 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    // [2] API 서버용 설정 (우선순위 2)
    // - 세션 미사용 (Stateless)
    // - JWT 인증 사용
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 끄기
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/error", "/favicon.ico",
                                "/oauth2/**", "/login/oauth2/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/api-docs/**",
                                "/api/auth/**"
                        ).permitAll()
                        // API 로그아웃은 인증된 사용자만
                        .requestMatchers("/api/auth/logout", "/api/auth/refresh").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler)
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}