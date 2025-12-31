package kr.java.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    STUDENT("일반 학생"),
    MAJOR("전공자"),
    ADMIN("관리자");

    private final String description;
}
