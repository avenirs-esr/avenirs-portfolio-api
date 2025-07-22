package fr.avenirsesr.portfolio.additionalskill.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class StudentAdditionalSkillConflictException extends BusinessException {
  public StudentAdditionalSkillConflictException() {
    super(EErrorCode.STUDENT_ADDITIONAL_SKILL_CONFLICT);
  }

  public StudentAdditionalSkillConflictException(String customMessage) {
    super(EErrorCode.STUDENT_ADDITIONAL_SKILL_CONFLICT, customMessage);
  }
}
