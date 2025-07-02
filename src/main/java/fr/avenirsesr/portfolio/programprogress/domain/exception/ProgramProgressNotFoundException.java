package fr.avenirsesr.portfolio.programprogress.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class ProgramProgressNotFoundException extends BusinessException {
  public ProgramProgressNotFoundException() {
    super(EErrorCode.PROGRAM_PROGRESS_NOT_FOUND);
  }

  public ProgramProgressNotFoundException(String customMessage) {
    super(EErrorCode.PROGRAM_PROGRESS_NOT_FOUND, customMessage);
  }
}
