package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FeedbackNotFoundException extends BusinessException {
  public FeedbackNotFoundException() {
    super(EErrorCode.FEEDBACK_NOT_FOUND);
  }
}
