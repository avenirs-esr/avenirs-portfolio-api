package fr.avenirsesr.portfolio.association.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class AssociationDoesNotExistException extends BusinessException {
  public AssociationDoesNotExistException() {
    super(EErrorCode.ASSOCIATION_NOT_FOUND);
  }

  public AssociationDoesNotExistException(String customMessage) {
    super(EErrorCode.ASSOCIATION_NOT_FOUND, customMessage);
  }
}
