package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityNotFoundException extends BusinessException {
  public DeclaredActivityNotFoundException() {
    super(EErrorCode.DECLARED_ACTIVITY_NOT_FOUND);
  }

  public DeclaredActivityNotFoundException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_NOT_FOUND, customMessage);
  }
}
