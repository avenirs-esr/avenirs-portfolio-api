package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityOperationNotAllowedException extends BusinessException {
  public SubActivityOperationNotAllowedException() {
    super(EErrorCode.SUB_ACTIVITY_OPERATION_NOT_ALLOWED);
  }
}
