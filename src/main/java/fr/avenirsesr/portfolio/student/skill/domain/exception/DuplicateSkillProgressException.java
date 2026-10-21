package fr.avenirsesr.portfolio.student.skill.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class DuplicateSkillProgressException extends BusinessException {
  public DuplicateSkillProgressException() {
    super(EErrorCode.SKILL_PROGRESS_ALREADY_EXISTS);
  }

  public DuplicateSkillProgressException(String customMessage) {
    super(EErrorCode.SKILL_PROGRESS_ALREADY_EXISTS, customMessage);
  }
}
