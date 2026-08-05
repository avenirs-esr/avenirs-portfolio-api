package fr.avenirsesr.portfolio.student.trace.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class TraceNotFoundException extends BusinessException {
  public TraceNotFoundException() {
    super(EErrorCode.TRACE_NOT_FOUND);
  }

  public TraceNotFoundException(String customMessage) {
    super(EErrorCode.TRACE_NOT_FOUND, customMessage);
  }
}
