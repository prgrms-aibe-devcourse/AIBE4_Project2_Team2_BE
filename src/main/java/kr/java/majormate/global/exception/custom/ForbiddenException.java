package kr.java.majormate.global.exception.custom;

import kr.java.majormate.global.exception.BusinessException;
import kr.java.majormate.global.exception.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
