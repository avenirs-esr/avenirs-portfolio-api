package fr.avenirsesr.portfolio.declaredskill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredSkillNotFoundException extends BusinessException {
  public DeclaredSkillNotFoundException() {
    super(EErrorCode.DECLARED_SKILL_NOT_FOUND);
  }

  public DeclaredSkillNotFoundException(String customMessage) {
    super(EErrorCode.DECLARED_SKILL_NOT_FOUND, customMessage);
  }
}
