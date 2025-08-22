package fr.avenirsesr.portfolio.shared.domain.model;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Setter;

@Setter
public abstract class DeletableAvenirsBaseModel extends AvenirsBaseModel {
  private Instant deletedAt;

  protected DeletableAvenirsBaseModel(
      UUID id, Instant deletedAt, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.deletedAt = deletedAt;
  }

  public Optional<Instant> getDeletedAt() {
    return Optional.ofNullable(deletedAt);
  }
}
