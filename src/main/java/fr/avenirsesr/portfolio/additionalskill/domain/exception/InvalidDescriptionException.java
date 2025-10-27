package fr.avenirsesr.portfolio.additionalskill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class InvalidDescriptionException extends BusinessException {
  public InvalidDescriptionException() {
    super(EErrorCode.ARGUMENT_TOO_LONG);
  }

  public InvalidDescriptionException(String customMessage) {
    super(EErrorCode.ARGUMENT_TOO_LONG, customMessage);
  }
}
