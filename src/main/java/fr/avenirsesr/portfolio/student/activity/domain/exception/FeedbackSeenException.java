package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FeedbackSeenException extends BusinessException {
  public FeedbackSeenException() {
    super(EErrorCode.FEEDBACK_SEEN);
  }
}
