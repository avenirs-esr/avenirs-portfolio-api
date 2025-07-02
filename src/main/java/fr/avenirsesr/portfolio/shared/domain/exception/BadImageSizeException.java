package fr.avenirsesr.portfolio.shared.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class BadImageSizeException extends BusinessException {

  public BadImageSizeException() {
    super(EErrorCode.BAD_IMAGE_SIZE);
  }

  public BadImageSizeException(String customMessage) {
    super(EErrorCode.BAD_IMAGE_SIZE, customMessage);
  }
}
