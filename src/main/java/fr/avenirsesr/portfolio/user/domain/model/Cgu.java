package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Cgu extends AvenirsBaseModel {
  private final User user;
  private final UUID versionId;
  private final Instant acceptedAt;

  private Cgu(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      User user,
      UUID versionId,
      Instant acceptedAt) {
    super(id, createdAt, updatedAt);
    this.user = user;
    this.versionId = versionId;
    this.acceptedAt = acceptedAt;
  }

  public static Cgu create(User user, UUID versionId) {
    Instant now = Instant.now();
    return new Cgu(UUID.randomUUID(), now, now, user, versionId, now);
  }

  public static Cgu toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      User user,
      UUID versionId,
      Instant acceptedAt) {
    return new Cgu(id, createdAt, updatedAt, user, versionId, acceptedAt);
  }
}
