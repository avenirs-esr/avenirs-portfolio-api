package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityUnsubscribedException extends BusinessException {
  public DeclaredActivityUnsubscribedException() {
    super(EErrorCode.DECLARED_ACTIVITY_UNSUBSCRIBED);
  }

  public DeclaredActivityUnsubscribedException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_UNSUBSCRIBED, customMessage);
  }
}
