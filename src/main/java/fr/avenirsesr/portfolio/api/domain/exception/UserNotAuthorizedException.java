package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class UserNotAuthorizedException extends BusinessException {
  public UserNotAuthorizedException() {
    super(EErrorCode.USER_NOT_AUTHORIZED);
  }

  public UserNotAuthorizedException(String customMessage) {
    super(EErrorCode.USER_NOT_AUTHORIZED, customMessage);
  }
}
