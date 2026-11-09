package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityPeriodOutsideMainActivityException extends BusinessException {
  public SubActivityPeriodOutsideMainActivityException() {
    super(EErrorCode.SUB_ACTIVITY_PERIOD_OUTSIDE_MAIN_ACTIVITY);
  }
}
