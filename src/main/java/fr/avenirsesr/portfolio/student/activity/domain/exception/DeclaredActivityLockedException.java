package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityLockedException extends BusinessException {
  public DeclaredActivityLockedException() {
    super(EErrorCode.DECLARED_ACTIVITY_LOCKED);
  }
}
