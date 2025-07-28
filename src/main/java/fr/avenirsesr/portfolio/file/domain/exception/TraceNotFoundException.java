package fr.avenirsesr.portfolio.file.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class TraceNotFoundException extends BusinessException {
  public TraceNotFoundException() {
    super(EErrorCode.TRACE_NOT_FOUND);
  }

  public TraceNotFoundException(String customMessage) {
    super(EErrorCode.TRACE_NOT_FOUND, customMessage);
  }
}
