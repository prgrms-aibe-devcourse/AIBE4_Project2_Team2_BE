package kr.java.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {

    PENDING("대기"),
    ACCEPTED("승인"),
    REJECTED("거절"),
    CANCELLED("취소");

    private final String description;
}
