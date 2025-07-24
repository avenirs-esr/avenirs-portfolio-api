package fr.avenirsesr.portfolio.additionalskill.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class DuplicateAdditionalSkillException extends BusinessException {
  public DuplicateAdditionalSkillException() {
    super(EErrorCode.STUDENT_ADDITIONAL_ALREADY_EXIST);
  }

  public DuplicateAdditionalSkillException(String customMessage) {
    super(EErrorCode.STUDENT_ADDITIONAL_ALREADY_EXIST, customMessage);
  }
}
