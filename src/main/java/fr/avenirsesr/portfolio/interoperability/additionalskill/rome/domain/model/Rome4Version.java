package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Rome4Version extends AvenirsBaseModel {
  private final int version;
  private final Instant lastModifiedDate;

  private Rome4Version(
      UUID id, Instant createdAt, Instant updatedAt, int version, Instant lastModifiedDate) {
    super(id, createdAt, updatedAt);
    this.version = version;
    this.lastModifiedDate = lastModifiedDate;
  }

  public static Rome4Version create(int version, Instant lastModifiedDate) {
    Instant now = Instant.now();
    return new Rome4Version(UUID.randomUUID(), now, now, version, lastModifiedDate);
  }

  public static Rome4Version toDomain(UUID id, int version, Instant lastModifiedDate) {
    Instant now = Instant.now();
    return new Rome4Version(id, now, now, version, lastModifiedDate);
  }
}
