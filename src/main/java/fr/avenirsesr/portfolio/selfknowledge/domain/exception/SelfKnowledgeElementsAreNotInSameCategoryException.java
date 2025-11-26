package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeElementsAreNotInSameCategoryException extends BusinessException {
  public SelfKnowledgeElementsAreNotInSameCategoryException() {
    super(EErrorCode.SELF_KNOWLEDGE_ELEMENTS_ARE_NOT_IN_SAME_CATEGORY);
  }

  public SelfKnowledgeElementsAreNotInSameCategoryException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_ELEMENTS_ARE_NOT_IN_SAME_CATEGORY, customMessage);
  }
}
