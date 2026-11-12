package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityAuthorizationChangeNotAllowedException extends BusinessException {
  public ActivityAuthorizationChangeNotAllowedException() {
    super(EErrorCode.ACTIVITY_AUTHORIZATION_CHANGE_NOT_ALLOWED);
  }
}
