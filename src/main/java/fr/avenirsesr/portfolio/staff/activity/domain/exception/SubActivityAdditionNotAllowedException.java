package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityAdditionNotAllowedException extends BusinessException {
  public SubActivityAdditionNotAllowedException() {
    super(EErrorCode.SUB_ACTIVITY_ADDITION_NOT_ALLOWED);
  }
}
