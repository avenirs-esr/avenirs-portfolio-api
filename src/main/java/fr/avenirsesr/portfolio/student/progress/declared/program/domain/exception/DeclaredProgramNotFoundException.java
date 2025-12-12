package fr.avenirsesr.portfolio.student.progress.declared.program.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredProgramNotFoundException extends BusinessException {
  public DeclaredProgramNotFoundException() {
    super(EErrorCode.DECLARED_PROGRAM_NOT_FOUND);
  }

  public DeclaredProgramNotFoundException(String customMessage) {
    super(EErrorCode.DECLARED_PROGRAM_NOT_FOUND, customMessage);
  }
}
