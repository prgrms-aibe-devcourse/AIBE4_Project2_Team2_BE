package kr.java.majormate.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterviewStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;
}
