package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DeclaredSkillProgressNotFoundException extends BusinessException {
  public DeclaredSkillProgressNotFoundException() {
    super(EErrorCode.DECLARED_SKILL_PROGRESS_NOT_FOUND);
  }

  public DeclaredSkillProgressNotFoundException(String customMessage) {
    super(EErrorCode.DECLARED_SKILL_PROGRESS_NOT_FOUND, customMessage);
  }
}
