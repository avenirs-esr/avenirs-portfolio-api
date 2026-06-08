package fr.avenirsesr.portfolio.notification.domain.port.input;

import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;

public interface NotificationService {
  void notify(BaseNotification notification);
}
