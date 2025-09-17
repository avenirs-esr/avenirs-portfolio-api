package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(EErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(String customMessage) {
    super(EErrorCode.USER_NOT_FOUND, customMessage);
  }
}
