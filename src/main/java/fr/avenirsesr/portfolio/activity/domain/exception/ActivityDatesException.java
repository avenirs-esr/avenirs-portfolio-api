package fr.avenirsesr.portfolio.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityDatesException extends BusinessException {
  public ActivityDatesException() {
    super(EErrorCode.ACTIVITY_DATES);
  }

  public ActivityDatesException(String customMessage) {
    super(EErrorCode.ACTIVITY_DATES, customMessage);
  }
}
