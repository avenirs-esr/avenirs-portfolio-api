package fr.avenirsesr.portfolio.notification.domain.exception;

import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;

public class NotificationNotFoundException extends BusinessException {
  public NotificationNotFoundException() {
    super(EErrorCode.NOTIFICATION_NOT_FOUND);
  }
}
