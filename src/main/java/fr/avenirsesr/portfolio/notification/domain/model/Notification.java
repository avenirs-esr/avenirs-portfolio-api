package fr.avenirsesr.portfolio.notification.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification extends AvenirsBaseModel {
  private final ENotificationType type;
  private final UUID elementId;
  private final User user;
  private final EUserCategory userCategory;
  private NotificationParameters parameters;
  private boolean seen;

  private Notification(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      ENotificationType type,
      UUID elementId,
      User user,
      EUserCategory userCategory,
      NotificationParameters parameters,
      boolean seen) {
    super(id, createdAt, updatedAt);
    this.type = type;
    this.elementId = elementId;
    this.user = user;
    this.userCategory = userCategory;
    this.parameters = parameters;
    this.seen = seen;
  }

  public static Notification create(
      ENotificationType type, UUID elementId, User user, NotificationParameters parameters) {
    Instant now = Instant.now();
    return new Notification(
        UUID.randomUUID(),
        now,
        now,
        type,
        elementId,
        user,
        type.getRestrictedTo(),
        parameters,
        false);
  }

  public static Notification toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      ENotificationType type,
      UUID elementId,
      User user,
      EUserCategory userCategory,
      NotificationParameters parameters,
      boolean seen) {
    return new Notification(
        id, createdAt, updatedAt, type, elementId, user, userCategory, parameters, seen);
  }
}
