package kr.java.aibe4_project2_team2_be.majormate.global.exception.custom;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;

public class NotFoundException extends BusinessExceptionNew {

    public NotFoundException(ErrorCodeNew errorCode) {
        super(errorCode);
    }

    public NotFoundException() {
        super(ErrorCodeNew.COMMON_404);
    }
}
