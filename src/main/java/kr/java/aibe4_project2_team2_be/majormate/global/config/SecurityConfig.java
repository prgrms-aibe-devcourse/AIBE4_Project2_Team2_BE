package kr.java.aibe4_project2_team2_be.majormate.global.config;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.CustomOAuth2UserService;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationFailureHandler;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.CustomOAuth2UserService;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationFailureHandler;
import kr.java.aibe4_project2_team2_be.majormate.domain.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import kr.java.aibe4_project2_team2_be.majormate.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

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

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configure(http))
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/error",
					"/favicon.ico",
					"/oauth2/**",
					"/login/oauth2/**",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/api-docs/**",
					// 아래는 제거해야 함
					"/api/**"
				).permitAll()
				.requestMatchers("/api/auth/logout", "/api/auth/refresh").authenticated()
				.requestMatchers("/api/auth/**").permitAll()
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(oauth2SuccessHandler)
				.failureHandler(oauth2FailureHandler)
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
