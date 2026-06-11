package fr.avenirsesr.portfolio.notification.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "notification",
    indexes = {@Index(name = "idx_notification_user_id", columnList = "user_id")})
@NoArgsConstructor
@Getter
@Setter
public class NotificationEntity extends AvenirsBaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ENotificationType type;

  @Column(name = "element_id", nullable = false)
  private UUID elementId;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_category")
  private EUserCategory userCategory;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Convert(converter = StringListJsonConverter.class)
  @Column(nullable = false, columnDefinition = "TEXT")
  private List<String> parameters;

  @Column(nullable = false)
  private boolean seen;

  private NotificationEntity(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      ENotificationType type,
      UUID elementId,
      UserEntity user,
      EUserCategory userCategory,
      List<String> parameters,
      boolean seen) {
    this.setId(id);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.type = type;
    this.elementId = elementId;
    this.user = user;
    this.userCategory = userCategory;
    this.parameters = parameters;
    this.seen = seen;
  }

  public static NotificationEntity of(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      ENotificationType type,
      UUID elementId,
      UserEntity user,
      EUserCategory userCategory,
      List<String> parameters,
      boolean seen) {
    return new NotificationEntity(
        id, createdAt, updatedAt, type, elementId, user, userCategory, parameters, seen);
  }
}
