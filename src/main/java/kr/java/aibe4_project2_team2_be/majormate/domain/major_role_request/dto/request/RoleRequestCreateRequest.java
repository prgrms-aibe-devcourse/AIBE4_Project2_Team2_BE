package kr.java.aibe4_project2_team2_be.majormate.domain.major_role_request.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleRequestCreateRequest {

	@NotBlank(message = "요청 내용은 필수입니다.")
	private String content;
}
