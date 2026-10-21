package fr.avenirsesr.portfolio.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SkillReferentialNotFoundException extends BusinessException {
  public SkillReferentialNotFoundException() {
    super(EErrorCode.SKILL_REFERENTIAL_NOT_FOUND);
  }

  public SkillReferentialNotFoundException(String customMessage) {
    super(EErrorCode.SKILL_REFERENTIAL_NOT_FOUND, customMessage);
  }
}
