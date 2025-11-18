package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeCategoryListIsEmptyException extends BusinessException {
  public SelfKnowledgeCategoryListIsEmptyException() {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_LIST_EMPTY);
  }

  public SelfKnowledgeCategoryListIsEmptyException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_LIST_EMPTY, customMessage);
  }
}
