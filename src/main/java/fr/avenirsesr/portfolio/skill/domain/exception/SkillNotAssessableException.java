package fr.avenirsesr.portfolio.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillNotAssessableException extends BusinessException {
  public SkillNotAssessableException() {
    super(EErrorCode.SKILL_NOT_ASSESSABLE);
  }

  public SkillNotAssessableException(String customMessage) {
    super(EErrorCode.SKILL_NOT_ASSESSABLE, customMessage);
  }
}
