package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityAssessmentException extends BusinessException {
  public ActivityAssessmentException() {
    super(EErrorCode.ACTIVITY_ASSESSMENT);
  }

  public ActivityAssessmentException(String customMessage) {
    super(EErrorCode.ACTIVITY_ASSESSMENT, customMessage);
  }
}
