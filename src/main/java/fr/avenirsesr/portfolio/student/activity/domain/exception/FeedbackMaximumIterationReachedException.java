package fr.avenirsesr.portfolio.student.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class FeedbackMaximumIterationReachedException extends BusinessException {
  public FeedbackMaximumIterationReachedException() {
    super(EErrorCode.FEEDBACK_MAXIMUM_ITERATION_REACHED);
  }
}
