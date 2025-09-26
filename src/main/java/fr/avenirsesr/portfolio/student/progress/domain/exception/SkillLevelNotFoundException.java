package fr.avenirsesr.portfolio.student.progress.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillLevelNotFoundException extends BusinessException {
  public SkillLevelNotFoundException() {
    super(EErrorCode.SKILL_NOT_FOUND);
  }

  public SkillLevelNotFoundException(String customMessage) {
    super(EErrorCode.SKILL_NOT_FOUND, customMessage);
  }
}
