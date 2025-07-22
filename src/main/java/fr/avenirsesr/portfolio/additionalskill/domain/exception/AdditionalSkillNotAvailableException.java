package fr.avenirsesr.portfolio.additionalskill.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class AdditionalSkillNotAvailableException extends BusinessException {
  public AdditionalSkillNotAvailableException() {
    super(EErrorCode.ADDITIONAL_SKILL_NOT_AVAILABLE);
  }

  public AdditionalSkillNotAvailableException(String customMessage) {
    super(EErrorCode.ADDITIONAL_SKILL_NOT_AVAILABLE, customMessage);
  }
}
