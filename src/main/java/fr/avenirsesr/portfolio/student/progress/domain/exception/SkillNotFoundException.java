package fr.avenirsesr.portfolio.student.progress.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillNotFoundException extends BusinessException {
  public SkillNotFoundException() {
    super(EErrorCode.SKILL_NOT_FOUND);
  }

  public SkillNotFoundException(String customMessage) {
    super(EErrorCode.SKILL_NOT_FOUND, customMessage);
  }
}
