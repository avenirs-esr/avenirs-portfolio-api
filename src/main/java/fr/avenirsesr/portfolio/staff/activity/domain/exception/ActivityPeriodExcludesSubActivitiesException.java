package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityPeriodExcludesSubActivitiesException extends BusinessException {
  public ActivityPeriodExcludesSubActivitiesException() {
    super(EErrorCode.ACTIVITY_PERIOD_EXCLUDES_SUB_ACTIVITIES);
  }
}
