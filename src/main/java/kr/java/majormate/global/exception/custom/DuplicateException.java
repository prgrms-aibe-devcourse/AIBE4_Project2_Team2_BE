package kr.java.majormate.global.exception.custom;

import kr.java.majormate.global.exception.BusinessException;
import kr.java.majormate.global.exception.ErrorCode;

public class DuplicateException extends BusinessException {

    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
