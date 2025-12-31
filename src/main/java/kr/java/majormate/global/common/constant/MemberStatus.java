package kr.java.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ENROLLED("재학"),
    LEAVE_OF_ABSENCE("휴학"),
    GRADUATED("졸업"),
    EXPELLED("제적");

    private final String description;
}
