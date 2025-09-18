package fr.avenirsesr.portfolio.additionalskill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class AdditionalSkillNotFoundException extends BusinessException {
  public AdditionalSkillNotFoundException() {
    super(EErrorCode.ADDITIONAL_SKILL_NOT_FOUND);
  }

  public AdditionalSkillNotFoundException(String customMessage) {
    super(EErrorCode.ADDITIONAL_SKILL_NOT_FOUND, customMessage);
  }
}
