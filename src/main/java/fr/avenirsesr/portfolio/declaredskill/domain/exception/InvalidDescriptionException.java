package fr.avenirsesr.portfolio.declaredskill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class InvalidDescriptionException extends BusinessException {
  public InvalidDescriptionException() {
    super(EErrorCode.DESCRIPTION_TOO_LONG);
  }

  public InvalidDescriptionException(String customMessage) {
    super(EErrorCode.DESCRIPTION_TOO_LONG, customMessage);
  }
}
