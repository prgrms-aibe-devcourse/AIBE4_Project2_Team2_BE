package kr.java.majormate.global.exception.custom;

import kr.java.majormate.global.exception.BusinessException;
import kr.java.majormate.global.exception.ErrorCode;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
