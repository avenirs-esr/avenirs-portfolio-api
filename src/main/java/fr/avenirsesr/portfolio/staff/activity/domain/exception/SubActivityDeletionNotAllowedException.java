package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityDeletionNotAllowedException extends BusinessException {
  public SubActivityDeletionNotAllowedException() {
    super(EErrorCode.SUB_ACTIVITY_DELETION_NOT_ALLOWED);
  }
}
