package fr.avenirsesr.portfolio.student.progress.imported.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityProgressNotFoundException extends BusinessException {
  public ActivityProgressNotFoundException() {
    super(EErrorCode.ACTIVITY_PROGRESS_NOT_FOUND);
  }

  public ActivityProgressNotFoundException(String customMessage) {
    super(EErrorCode.ACTIVITY_PROGRESS_NOT_FOUND, customMessage);
  }
}
