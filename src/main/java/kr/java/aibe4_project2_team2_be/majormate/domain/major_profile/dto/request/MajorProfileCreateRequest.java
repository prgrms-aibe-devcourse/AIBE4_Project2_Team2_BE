package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MajorProfileCreateRequest {
	@NotBlank
	private String title;
	private String content;
	private List<String> tags;

}
