package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeCategoryNotFoundException extends BusinessException {
  public SelfKnowledgeCategoryNotFoundException() {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_FOUND);
  }

  public SelfKnowledgeCategoryNotFoundException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_FOUND, customMessage);
  }
}
