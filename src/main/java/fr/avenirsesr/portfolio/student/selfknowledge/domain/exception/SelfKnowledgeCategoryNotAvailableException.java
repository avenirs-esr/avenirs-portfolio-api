package fr.avenirsesr.portfolio.student.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeCategoryNotAvailableException extends BusinessException {
  public SelfKnowledgeCategoryNotAvailableException() {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_AVAILABLE);
  }

  public SelfKnowledgeCategoryNotAvailableException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_AVAILABLE, customMessage);
  }
}
