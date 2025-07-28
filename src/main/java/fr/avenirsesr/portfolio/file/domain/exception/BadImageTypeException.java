package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class BadImageTypeException extends BusinessException {

  public BadImageTypeException() {
    super(EErrorCode.BAD_IMAGE_TYPE);
  }

  public BadImageTypeException(String customMessage) {
    super(EErrorCode.BAD_IMAGE_TYPE, customMessage);
  }
}
