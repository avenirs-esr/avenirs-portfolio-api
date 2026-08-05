package fr.avenirsesr.portfolio.student.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeCategoryNotLinkedToStudentException extends BusinessException {
  public SelfKnowledgeCategoryNotLinkedToStudentException() {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_LINKED);
  }

  public SelfKnowledgeCategoryNotLinkedToStudentException(String customMessage) {
    super(EErrorCode.SELF_KNOWLEDGE_CATEGORY_NOT_LINKED, customMessage);
  }
}
