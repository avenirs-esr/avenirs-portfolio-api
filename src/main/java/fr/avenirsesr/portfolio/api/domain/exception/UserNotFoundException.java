package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(EErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(String customMessage) {
    super(EErrorCode.USER_NOT_FOUND, customMessage);
  }
}
