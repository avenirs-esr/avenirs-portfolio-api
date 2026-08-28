package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FeedbackSubmittedException extends BusinessException {
  public FeedbackSubmittedException() {
    super(EErrorCode.FEEDBACK_SUBMITTED);
  }
}
