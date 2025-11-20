package fr.avenirsesr.portfolio.selfknowledge.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SelfKnowledgeInvalidRatingException extends BusinessException {
  public SelfKnowledgeInvalidRatingException() {
    super(EErrorCode.RATING_OUT_OF_BOUNCE);
  }

  public SelfKnowledgeInvalidRatingException(String customMessage) {
    super(EErrorCode.RATING_OUT_OF_BOUNCE, customMessage);
  }
}
