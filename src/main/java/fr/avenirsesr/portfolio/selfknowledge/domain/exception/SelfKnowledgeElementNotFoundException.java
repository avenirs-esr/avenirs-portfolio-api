package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeElementNotFoundException extends BusinessException {
  public SelfKnowledgeElementNotFoundException() {
    super(EErrorCode.SELF_KNOWLEDGE_ELEMENT_NOT_FOUND);
  }

  public SelfKnowledgeElementNotFoundException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_ELEMENT_NOT_FOUND, customMessage);
  }
}
