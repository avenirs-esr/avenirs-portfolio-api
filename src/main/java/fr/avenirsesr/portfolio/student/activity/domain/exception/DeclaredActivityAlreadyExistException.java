package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredActivityAlreadyExistException extends BusinessException {
  public DeclaredActivityAlreadyExistException() {
    super(EErrorCode.DECLARED_ACTIVITY_ALREADY_EXIST);
  }

  public DeclaredActivityAlreadyExistException(String customMessage) {
    super(EErrorCode.DECLARED_ACTIVITY_ALREADY_EXIST, customMessage);
  }
}
