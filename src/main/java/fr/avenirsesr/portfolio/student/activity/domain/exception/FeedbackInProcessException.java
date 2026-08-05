package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FeedbackInProcessException extends BusinessException {
  public FeedbackInProcessException() {
    super(EErrorCode.FEEDBACK_IN_PROCESS);
  }
}
