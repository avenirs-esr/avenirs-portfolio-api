package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityTargetNotAccessibleException extends BusinessException {
  public ActivityTargetNotAccessibleException() {
    super(EErrorCode.ACTIVITY_TARGET_NOT_ACCESSIBLE);
  }
}
