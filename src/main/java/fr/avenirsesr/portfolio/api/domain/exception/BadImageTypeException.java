package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class BadImageTypeException extends BusinessException {

  public BadImageTypeException() {
    super(EErrorCode.BAD_IMAGE_TYPE);
  }

  public BadImageTypeException(String customMessage) {
    super(EErrorCode.BAD_IMAGE_TYPE, customMessage);
  }
}
