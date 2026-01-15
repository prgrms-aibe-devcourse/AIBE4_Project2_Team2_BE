package kr.java.aibe4_project2_team2_be.majormate.global.exception.custom;

import kr.java.aibe4_project2_team2_be.majormate.global.exception.BusinessException;
import kr.java.aibe4_project2_team2_be.majormate.global.exception.ErrorCode;

public class BadRequestException extends BusinessException {

	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
