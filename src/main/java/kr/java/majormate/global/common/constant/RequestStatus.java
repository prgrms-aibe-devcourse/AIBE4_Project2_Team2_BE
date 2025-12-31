package kr.java.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStatus {

    PENDING("승인 대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;
}
