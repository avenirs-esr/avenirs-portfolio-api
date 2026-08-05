package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityDatesException extends BusinessException {
  public DeclaredActivityDatesException() {
    super(EErrorCode.ACTIVITY_DATES);
  }

  public DeclaredActivityDatesException(String customMessage) {
    super(EErrorCode.ACTIVITY_DATES, customMessage);
  }
}
