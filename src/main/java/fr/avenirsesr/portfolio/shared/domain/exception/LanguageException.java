package fr.avenirsesr.portfolio.shared.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class LanguageException extends BusinessException {
  public LanguageException() {
    super(EErrorCode.LANGUAGE_NOT_SUPPORTED);
  }

  public LanguageException(String customMessage) {
    super(EErrorCode.LANGUAGE_NOT_SUPPORTED, customMessage);
  }
}
