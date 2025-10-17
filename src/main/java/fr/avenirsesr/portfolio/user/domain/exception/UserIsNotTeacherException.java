package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class UserIsNotTeacherException extends BusinessException {
  public UserIsNotTeacherException() {
    super(EErrorCode.USER_IS_NOT_TEACHER_EXCEPTION);
  }

  public UserIsNotTeacherException(String message) {
    super(EErrorCode.USER_IS_NOT_TEACHER_EXCEPTION, message);
  }
}
