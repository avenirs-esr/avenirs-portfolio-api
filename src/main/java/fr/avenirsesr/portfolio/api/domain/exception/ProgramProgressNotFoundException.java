package fr.avenirsesr.portfolio.api.domain.exception;

import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;

public class ProgramProgressNotFoundException extends BusinessException {
  public ProgramProgressNotFoundException() {
    super(EErrorCode.PROGRAM_PROGRESS_NOT_FOUND);
  }

  public ProgramProgressNotFoundException(String customMessage) {
    super(EErrorCode.PROGRAM_PROGRESS_NOT_FOUND, customMessage);
  }
}
