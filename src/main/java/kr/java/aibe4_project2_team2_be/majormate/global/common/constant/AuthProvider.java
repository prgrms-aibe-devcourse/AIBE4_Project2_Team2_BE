package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {

    LOCAL("로컬"),
    GOOGLE("구글"),
    GITHUB("깃허브"),
    KAKAO("카카오");

    private final String description;
}
