package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class SubActivityFieldNotAllowedException extends FieldValidationException {
  public SubActivityFieldNotAllowedException(String fieldName) {
    super(EErrorCode.SUB_ACTIVITY_FIELD_NOT_ALLOWED, fieldName);
  }
}
