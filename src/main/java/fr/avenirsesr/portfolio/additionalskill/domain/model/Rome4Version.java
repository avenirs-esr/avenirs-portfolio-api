package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rome4Version extends AvenirsBaseModel {
  private UUID id;
  private Integer version;
  private Instant lastModifiedDate;

  private Rome4Version(
      UUID id, Instant createdAt, Instant updatedAt, Integer version, Instant lastModifiedDate) {
    super(id, createdAt, updatedAt);
    this.version = version;
    this.lastModifiedDate = lastModifiedDate;
  }

  public static Rome4Version create(Integer version, Instant lastModifiedDate) {
    Instant now = Instant.now();
    return new Rome4Version(UUID.randomUUID(), now, now, version, lastModifiedDate);
  }

  public static Rome4Version toDomain(UUID id, Integer version, Instant lastModifiedDate) {
    Instant now = Instant.now();
    return new Rome4Version(id, now, now, version, lastModifiedDate);
  }
}
