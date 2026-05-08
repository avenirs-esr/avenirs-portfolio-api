package fr.avenirsesr.portfolio.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityDraftNotFoundException extends BusinessException {
  public ActivityDraftNotFoundException() {
    super(EErrorCode.ACTIVITY_DRAFT_NOT_FOUND);
  }

  public ActivityDraftNotFoundException(String customMessage) {
    super(EErrorCode.ACTIVITY_DRAFT_NOT_FOUND, customMessage);
  }
}
