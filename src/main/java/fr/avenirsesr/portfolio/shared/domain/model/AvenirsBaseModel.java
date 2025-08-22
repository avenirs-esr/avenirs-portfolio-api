package fr.avenirsesr.portfolio.shared.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class AvenirsBaseModel {
  private final UUID id;
  private final Instant createdAt;
  private final Instant updatedAt;

  protected AvenirsBaseModel(UUID id, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AvenirsBaseModel that = (AvenirsBaseModel) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + id + ']';
  }
}
