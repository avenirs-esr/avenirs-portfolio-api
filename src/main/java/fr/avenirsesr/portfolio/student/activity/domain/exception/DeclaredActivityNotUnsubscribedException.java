package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityNotUnsubscribedException extends BusinessException {
  public DeclaredActivityNotUnsubscribedException() {
    super(EErrorCode.DECLARED_ACTIVITY_NOT_UNSUBSCRIBED);
  }

  public DeclaredActivityNotUnsubscribedException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_NOT_UNSUBSCRIBED, customMessage);
  }
}
