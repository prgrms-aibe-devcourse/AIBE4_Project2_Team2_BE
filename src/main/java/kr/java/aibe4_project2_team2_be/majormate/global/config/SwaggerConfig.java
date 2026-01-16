package kr.java.aibe4_project2_team2_be.majormate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		String jwtSchemeName = "JWT Authentication";

		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList(jwtSchemeName);

		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT"));

		return new OpenAPI()
			.info(new Info()
				.title("MajorMate API")
				.description("대학교 진학 및 전공 선택에 고민이 많은 학생들을 위한 진로 길잡이 플랫폼 API 명세서")
				.version("1.0.0"))
			.addSecurityItem(securityRequirement)
			.components(components);
	}
}
