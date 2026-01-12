package kr.java.aibe4_project2_team2_be.majormate.global.exception.custom;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;

public class ValidationException extends BusinessExceptionNew {

    public ValidationException(ErrorCodeNew errorCode) {
        super(errorCode);
    }

    public ValidationException() {
        super(ErrorCodeNew.COMMON_400);
    }
}
