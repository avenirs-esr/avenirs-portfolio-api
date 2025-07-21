package fr.avenirsesr.portfolio.shared.domain.model;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

public abstract class AvenirsBaseModel {
  @Getter private final UUID id;

  protected AvenirsBaseModel(UUID id) {
    this.id = id;
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
