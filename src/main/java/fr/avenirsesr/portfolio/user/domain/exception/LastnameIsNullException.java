package fr.avenirsesr.portfolio.user.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class LastnameIsNullException extends BusinessException {
  public LastnameIsNullException() {
    super(EErrorCode.LASTNAME_IS_NULL);
  }

  public LastnameIsNullException(String message) {
    super(EErrorCode.LASTNAME_IS_NULL, message);
  }
}
