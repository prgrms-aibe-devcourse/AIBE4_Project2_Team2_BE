package kr.java.aibe4_project2_team2_be.majormate.domain.member.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberRole;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberStatus;
import kr.java.aibe4_project2_team2_be.majormate.global.common.constant.MemberType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String username;
    private String email;
    private String name;
    private String nickname;
    private MemberType memberType;
    private MemberStatus memberStatus;
    private MemberRole role;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .memberType(member.getMemberType())
                .memberStatus(member.getMemberStatus())
                .role(member.getRole())
                .build();
    }
}
