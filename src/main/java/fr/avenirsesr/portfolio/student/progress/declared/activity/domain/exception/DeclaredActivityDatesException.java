package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityDatesException extends BusinessException {
  public DeclaredActivityDatesException() {
    super(EErrorCode.DECLARED_ACTIVITY_DATES);
  }

  public DeclaredActivityDatesException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_DATES, customMessage);
  }
}
