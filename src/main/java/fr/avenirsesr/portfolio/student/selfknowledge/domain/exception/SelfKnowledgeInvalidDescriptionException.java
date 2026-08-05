package fr.avenirsesr.portfolio.student.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeInvalidDescriptionException extends BusinessException {
  public SelfKnowledgeInvalidDescriptionException() {
    super(EErrorCode.DESCRIPTION_TOO_LONG);
  }

  public SelfKnowledgeInvalidDescriptionException(String customMessage) {
    super(EErrorCode.DESCRIPTION_TOO_LONG, customMessage);
  }
}
