package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfileTag;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MajorCardResponse {
	@NotBlank
	private Long id;
	private String nickname;
	private String university;
	private String major;
	private String title;
	private String profileImageUrl;
	private List<String> tags;
	private Long likeCount;
	private boolean isActive;

	public static MajorCardResponse of(MajorProfile profile, MemberAcademic academic) {
		return MajorCardResponse.builder()
			.id(profile.getMajorProfileId())
			.nickname(profile.getMemberProfile().getNickname())
			.university(academic.getUniversity())
			.major(academic.getMajor())
			.title(profile.getTitle())
			.profileImageUrl(profile.getMemberProfile().getProfileImageUrl())
			.tags(profile.getTags().stream()
				.map(MajorProfileTag::getTagName)
				.collect(Collectors.toList()))
			.likeCount(0L) // 추후 구현
			.isActive(profile.isActive())
			.build();
	}
}
