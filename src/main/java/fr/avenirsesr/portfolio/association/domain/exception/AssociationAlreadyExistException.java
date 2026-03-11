package fr.avenirsesr.portfolio.association.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class AssociationAlreadyExistException extends BusinessException {
  public AssociationAlreadyExistException() {
    super(EErrorCode.ASSOCIATION_ALREADY_EXIST);
  }

  public AssociationAlreadyExistException(String message) {
    super(EErrorCode.ASSOCIATION_ALREADY_EXIST, message);
  }
}
