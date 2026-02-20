package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityAlreadyFinishedException extends BusinessException {
  public DeclaredActivityAlreadyFinishedException() {
    super(EErrorCode.DECLARED_ACTIVITY_ALREADY_FINISHED);
  }

  public DeclaredActivityAlreadyFinishedException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_ALREADY_FINISHED, customMessage);
  }
}
