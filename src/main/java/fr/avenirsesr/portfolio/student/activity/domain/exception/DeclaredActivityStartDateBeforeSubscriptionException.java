package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityStartDateBeforeSubscriptionException extends BusinessException {
  public DeclaredActivityStartDateBeforeSubscriptionException() {
    super(EErrorCode.DECLARED_ACTIVITY_START_DATE_BEFORE_SUBSCRIPTION);
  }

  public DeclaredActivityStartDateBeforeSubscriptionException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_START_DATE_BEFORE_SUBSCRIPTION, customMessage);
  }
}
