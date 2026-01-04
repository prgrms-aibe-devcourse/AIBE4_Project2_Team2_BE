package kr.java.aibe4_project2_team2_be.majormate.domain.auth.dto.response;

import kr.java.aibe4_project2_team2_be.majormate.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {

    private Long id;
    private String email;
    private String name;
    private String nickname;

    public static SignupResponse from(Member member) {
        return SignupResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .build();
    }
}
