package fr.avenirsesr.portfolio.student.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillProgressNotFoundException extends BusinessException {
  public SkillProgressNotFoundException() {
    super(EErrorCode.SKILL_PROGRESS_NOT_FOUND);
  }

  public SkillProgressNotFoundException(String customMessage) {
    super(EErrorCode.SKILL_PROGRESS_NOT_FOUND, customMessage);
  }
}
