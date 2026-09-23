package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityTargetRemovalNotAllowedException extends BusinessException {
  public ActivityTargetRemovalNotAllowedException() {
    super(EErrorCode.ACTIVITY_TARGET_REMOVAL_NOT_ALLOWED);
  }
}
