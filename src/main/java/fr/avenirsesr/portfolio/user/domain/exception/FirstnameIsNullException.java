package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FirstnameIsNullException extends BusinessException {
  public FirstnameIsNullException() {
    super(EErrorCode.FIRSTNAME_IS_NULL);
  }

  public FirstnameIsNullException(String message) {
    super(EErrorCode.FIRSTNAME_IS_NULL, message);
  }
}
