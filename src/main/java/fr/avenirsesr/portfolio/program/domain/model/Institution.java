package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Institution extends AvenirsBaseModel {
  private final String name;

  private Institution(UUID id, String name, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.name = name;
  }

  public static Institution create(UUID id, String name) {
    return new Institution(id, name, Instant.now(), Instant.now());
  }

  public static Institution toDomain(UUID id, String name, Instant createdAt, Instant updatedAt) {

    return new Institution(id, name, createdAt, updatedAt);
  }
}
