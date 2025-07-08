package fr.avenirsesr.portfolio.program.domain.exception;

import fr.avenirsesr.portfolio.shared.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;

public class TrainingPathNotFoundException extends BusinessException {
  public TrainingPathNotFoundException() {
    super(EErrorCode.TRAINING_PATH_NOT_FOUND);
  }

  public TrainingPathNotFoundException(String customMessage) {
    super(EErrorCode.TRAINING_PATH_NOT_FOUND, customMessage);
  }
}
