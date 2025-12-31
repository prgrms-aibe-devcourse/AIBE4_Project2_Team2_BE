package kr.java.majormate.global.exception.custom;

import kr.java.majormate.global.exception.BusinessException;
import kr.java.majormate.global.exception.ErrorCode;

public class ValidationException extends BusinessException {

    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException() {
        super(ErrorCode.INVALID_INPUT_VALUE);
    }
}
