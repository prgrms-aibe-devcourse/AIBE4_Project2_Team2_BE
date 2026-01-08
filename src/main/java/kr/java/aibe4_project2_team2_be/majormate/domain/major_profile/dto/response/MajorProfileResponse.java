package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response;

import java.util.List;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MajorProfileResponse {
	private Long id;
	private String title;
	private String content;
	private List<String> tags;

	public static MajorProfileResponse from(MajorProfile profile) {
		return MajorProfileResponse.builder()
			.id(profile.getMajorProfileId())
			.title(profile.getTitle())
			.content(profile.getContent())
			.tags(profile.getTags())
			.build();
	}
}
