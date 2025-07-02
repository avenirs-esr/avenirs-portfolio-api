package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class UserIsNotStudentException extends BusinessException {
  public UserIsNotStudentException() {
    super(EErrorCode.USER_IS_NOT_STUDENT_EXCEPTION);
  }

  public UserIsNotStudentException(String customMessage) {
    super(EErrorCode.USER_IS_NOT_STUDENT_EXCEPTION, customMessage);
  }
}
