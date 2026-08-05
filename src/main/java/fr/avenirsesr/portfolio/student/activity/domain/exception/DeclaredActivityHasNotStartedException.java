package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityHasNotStartedException extends BusinessException {
  public DeclaredActivityHasNotStartedException() {
    super(EErrorCode.DECLARED_ACTIVITY_HAS_NOT_STARTED);
  }

  public DeclaredActivityHasNotStartedException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_HAS_NOT_STARTED, customMessage);
  }
}
