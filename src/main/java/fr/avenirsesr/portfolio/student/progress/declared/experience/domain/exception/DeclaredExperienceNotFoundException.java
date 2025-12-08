package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredExperienceNotFoundException extends BusinessException {
  public DeclaredExperienceNotFoundException() {
    super(EErrorCode.DECLARED_EXPERIENCE_NOT_FOUND);
  }

  public DeclaredExperienceNotFoundException(String message) {
    super(EErrorCode.DECLARED_EXPERIENCE_NOT_FOUND, message);
  }
}
