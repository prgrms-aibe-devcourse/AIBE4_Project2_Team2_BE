package kr.java.aibe4_project2_team2_be.majormate.global.exception.custom;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;

public class UnauthorizedException extends BusinessExceptionNew {

    public UnauthorizedException(ErrorCodeNew errorCode) {
        super(errorCode);
    }

    public UnauthorizedException() {
        super(ErrorCodeNew.AUTH_401);
    }
}
