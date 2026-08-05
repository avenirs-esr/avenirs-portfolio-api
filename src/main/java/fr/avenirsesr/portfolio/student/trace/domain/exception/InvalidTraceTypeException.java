package fr.avenirsesr.portfolio.student.trace.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class InvalidTraceTypeException extends BusinessException {
  public InvalidTraceTypeException() {
    super(EErrorCode.INVALID_TRACE_TYPE);
  }

  public InvalidTraceTypeException(String customMessage) {
    super(EErrorCode.INVALID_TRACE_TYPE, customMessage);
  }
}
