package fr.avenirsesr.portfolio.student.progress.imported.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class AdditionalSkillProgressNotFoundException extends BusinessException {
  public AdditionalSkillProgressNotFoundException() {
    super(EErrorCode.ADDITIONAL_SKILL_PROGRESS_NOT_FOUND);
  }

  public AdditionalSkillProgressNotFoundException(String customMessage) {
    super(EErrorCode.ADDITIONAL_SKILL_PROGRESS_NOT_FOUND, customMessage);
  }
}
