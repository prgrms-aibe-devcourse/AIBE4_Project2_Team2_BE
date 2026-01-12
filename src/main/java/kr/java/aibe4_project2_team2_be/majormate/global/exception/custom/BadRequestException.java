package kr.java.aibe4_project2_team2_be.majormate.global.exception.custom;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessExceptionNew;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCodeNew;

public class BadRequestException extends BusinessExceptionNew {

	public BadRequestException(ErrorCodeNew errorCode) {
		super(errorCode);
	}
}
