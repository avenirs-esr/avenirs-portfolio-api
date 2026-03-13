package fr.avenirsesr.portfolio.association.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Association extends AvenirsBaseModel {
  private final UUID id1;
  private final UUID id2;
  private final EAssociationType associationType;

  private Association(
      UUID id,
      UUID id1,
      UUID id2,
      EAssociationType associationType,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.id1 = id1;
    this.id2 = id2;
    this.associationType = associationType;
  }

  public static Association create(UUID id1, UUID id2, EAssociationType associationType) {
    Instant now = Instant.now();
    return new Association(UUID.randomUUID(), id1, id2, associationType, now, now);
  }

  public static Association toDomain(
      UUID id,
      UUID id1,
      UUID id2,
      EAssociationType associationType,
      Instant createdAt,
      Instant updatedAt) {
    return new Association(id, id1, id2, associationType, createdAt, updatedAt);
  }
}
