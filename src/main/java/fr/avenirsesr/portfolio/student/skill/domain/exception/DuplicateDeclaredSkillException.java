package fr.avenirsesr.portfolio.student.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DuplicateDeclaredSkillException extends BusinessException {
  public DuplicateDeclaredSkillException() {
    super(EErrorCode.STUDENT_DECLARED_ALREADY_EXIST);
  }

  public DuplicateDeclaredSkillException(String customMessage) {
    super(EErrorCode.STUDENT_DECLARED_ALREADY_EXIST, customMessage);
  }
}
