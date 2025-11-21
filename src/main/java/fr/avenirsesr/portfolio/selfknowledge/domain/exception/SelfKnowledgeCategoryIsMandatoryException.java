package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeCategoryIsMandatoryException extends BusinessException {
  public SelfKnowledgeCategoryIsMandatoryException() {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_IS_MANDATORY);
  }

  public SelfKnowledgeCategoryIsMandatoryException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_IS_MANDATORY, customMessage);
  }
}
