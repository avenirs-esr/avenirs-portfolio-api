package fr.avenirsesr.portfolio.student.association.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class MaximumAssociationReachedException extends BusinessException {
  public MaximumAssociationReachedException() {
    super(EErrorCode.MAXIMUM_ALLOWED_ASSOCIATIONS_REACHED);
  }

  public MaximumAssociationReachedException(String message) {
    super(EErrorCode.MAXIMUM_ALLOWED_ASSOCIATIONS_REACHED, message);
  }
}
