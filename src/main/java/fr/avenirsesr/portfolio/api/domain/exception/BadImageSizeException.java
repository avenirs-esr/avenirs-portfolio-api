package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class BadImageSizeException extends BusinessException {

  public BadImageSizeException() {
    super(EErrorCode.BAD_IMAGE_SIZE);
  }

  public BadImageSizeException(String customMessage) {
    super(EErrorCode.BAD_IMAGE_SIZE, customMessage);
  }
}
