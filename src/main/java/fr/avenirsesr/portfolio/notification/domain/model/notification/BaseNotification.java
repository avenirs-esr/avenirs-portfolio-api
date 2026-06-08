package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import java.util.List;
import java.util.UUID;

public abstract class BaseNotification {

  protected final User user;
  protected final ENotificationType type;
  protected final UUID elementId;

  protected BaseNotification(User user, ENotificationType type, UUID elementId) {
    this.user = user;
    this.type = type;
    this.elementId = elementId;
  }

  protected abstract List<String> buildParameters();

  public Notification build() {
    return Notification.create(type, elementId, user, buildParameters());
  }
}
