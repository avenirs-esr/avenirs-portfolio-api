package fr.avenirsesr.portfolio.staff.activity.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class ActivityLeaderRequiredException extends BusinessException {
  public ActivityLeaderRequiredException() {
    super(
        EErrorCode.NOT_NULL,
        "An activity requires at least one leader holding the leader, edit, publish"
            + " and manage authorizations rights");
  }
}
