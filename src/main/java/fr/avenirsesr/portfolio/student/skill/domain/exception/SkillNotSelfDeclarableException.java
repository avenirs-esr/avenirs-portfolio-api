package fr.avenirsesr.portfolio.student.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillNotSelfDeclarableException extends BusinessException {
  public SkillNotSelfDeclarableException() {
    super(EErrorCode.SKILL_NOT_SELF_DECLARABLE);
  }

  public SkillNotSelfDeclarableException(String customMessage) {
    super(EErrorCode.SKILL_NOT_SELF_DECLARABLE, customMessage);
  }
}
