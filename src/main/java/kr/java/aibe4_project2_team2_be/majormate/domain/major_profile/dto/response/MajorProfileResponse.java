package kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfile;
import kr.java.aibe4_project2_team2_be.majormate.domain.major_profile.entity.MajorProfileTag;
import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.MemberAcademic;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MajorProfileResponse {
	private Long id;
	private Long memberId;
	private String name;
	private String nickname;
	private String university;
	private String major;
	private String profileImageUrl;
	private String title;
	private String content;
	private List<String> tags;
	private Long likeCount;
	private boolean isLiked;
	private boolean isActive;

	public static MajorProfileResponse of(MajorProfile profile, MemberAcademic academic, Long likeCount, boolean isLiked) {
		return MajorProfileResponse.builder()
			.id(profile.getMajorProfileId())
			.memberId(academic.getMemberProfile().getMemberId())
			.name(profile.getMemberProfile().getName())
			.nickname(profile.getMemberProfile().getNickname())
			.university(academic.getUniversity())
			.major(academic.getMajor())
			.profileImageUrl(profile.getMemberProfile().getProfileImageUrl())
			.title(profile.getTitle())
			.content(profile.getContent())
			.tags(profile.getTags().stream()
				.map(MajorProfileTag::getTagName)
				.collect(Collectors.toList()))
			.likeCount(likeCount)
			.isLiked(isLiked)
			.isActive(profile.isActive())
			.build();
	}
}
