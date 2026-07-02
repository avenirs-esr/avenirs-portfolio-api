package fr.avenirsesr.portfolio.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityUnpublishedException extends BusinessException {
  public ActivityUnpublishedException() {
    super(EErrorCode.ACTIVITY_UNPUBLISHED);
  }
}
