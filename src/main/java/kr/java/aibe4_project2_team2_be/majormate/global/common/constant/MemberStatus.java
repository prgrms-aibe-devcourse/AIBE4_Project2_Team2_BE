package kr.java.aibe4_project2_team2_be.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ENROLLED("재학"),
    GRADUATED("졸업"),
    HIGHSCHOOL("고등학생");

    private final String description;
}
