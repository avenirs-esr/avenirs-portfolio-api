package fr.avenirsesr.portfolio.notification.domain.port.input;

import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import java.util.UUID;

public interface NotificationService {
  void notify(BaseNotification notification);

  void markAsSeen(UUID id);
}
