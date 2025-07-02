package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class UserNotAuthorizedException extends BusinessException {
  public UserNotAuthorizedException() {
    super(EErrorCode.USER_NOT_AUTHORIZED);
  }

  public UserNotAuthorizedException(String customMessage) {
    super(EErrorCode.USER_NOT_AUTHORIZED, customMessage);
  }
}
