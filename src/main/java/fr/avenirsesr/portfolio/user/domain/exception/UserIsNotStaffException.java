package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class UserIsNotStaffException extends BusinessException {
  public UserIsNotStaffException() {
    super(EErrorCode.USER_IS_NOT_STAFF_EXCEPTION);
  }

  public UserIsNotStaffException(String message) {
    super(EErrorCode.USER_IS_NOT_STAFF_EXCEPTION, message);
  }
}
