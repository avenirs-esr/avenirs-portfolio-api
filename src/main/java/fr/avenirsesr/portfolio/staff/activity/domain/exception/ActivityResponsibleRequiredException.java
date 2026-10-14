package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityResponsibleRequiredException extends BusinessException {
  public ActivityResponsibleRequiredException() {
    super(
        EErrorCode.NOT_NULL,
        "An activity requires at least one responsible holding the responsible, edit, publish"
            + " and manage authorizations rights");
  }
}
