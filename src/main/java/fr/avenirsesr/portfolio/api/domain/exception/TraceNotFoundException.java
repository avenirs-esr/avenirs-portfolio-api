package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class TraceNotFoundException extends BusinessException {
  public TraceNotFoundException() {
    super(EErrorCode.TRACE_NOT_FOUND);
  }

  public TraceNotFoundException(String customMessage) {
    super(EErrorCode.TRACE_NOT_FOUND, customMessage);
  }
}
