package fr.avenirsesr.portfolio.student.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeInvalidTitleException extends BusinessException {
  public SelfKnowledgeInvalidTitleException() {
    super(EErrorCode.TITLE_TOO_LONG);
  }

  public SelfKnowledgeInvalidTitleException(String customMessage) {
    super(EErrorCode.TITLE_TOO_LONG, customMessage);
  }
}
