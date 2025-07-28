package fr.avenirsesr.portfolio.student.progress.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class StudentProgressNotFoundException extends BusinessException {
  public StudentProgressNotFoundException() {
    super(EErrorCode.STUDENT_PROGRESS_NOT_FOUND);
  }

  public StudentProgressNotFoundException(String customMessage) {
    super(EErrorCode.STUDENT_PROGRESS_NOT_FOUND, customMessage);
  }
}
