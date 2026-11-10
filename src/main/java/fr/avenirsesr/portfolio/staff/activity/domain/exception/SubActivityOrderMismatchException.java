package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityOrderMismatchException extends BusinessException {
  public SubActivityOrderMismatchException() {
    super(EErrorCode.SUB_ACTIVITY_ORDER_MISMATCH);
  }
}
