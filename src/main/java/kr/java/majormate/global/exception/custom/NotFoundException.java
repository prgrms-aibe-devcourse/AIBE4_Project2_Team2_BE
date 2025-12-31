package kr.java.majormate.global.exception.custom;

import kr.java.majormate.global.exception.BusinessException;
import kr.java.majormate.global.exception.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }
}
