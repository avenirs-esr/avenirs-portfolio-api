package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityNotFoundException extends BusinessException {
  public ActivityNotFoundException() {
    super(EErrorCode.ACTIVITY_NOT_FOUND);
  }

  public ActivityNotFoundException(String customMessage) {
    super(EErrorCode.ACTIVITY_NOT_FOUND, customMessage);
  }
}
