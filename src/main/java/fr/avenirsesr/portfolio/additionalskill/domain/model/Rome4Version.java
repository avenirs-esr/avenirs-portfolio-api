package fr.avenirsesr.portfolio.additionalskill.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rome4Version {
  private UUID id;
  private Integer version;
  private Instant lastModifiedDate;

  private Rome4Version(UUID id, Integer version, Instant lastModifiedDate) {
    this.id = id;
    this.version = version;
    this.lastModifiedDate = lastModifiedDate;
  }

  public static Rome4Version create(Integer version, Instant lastModifiedDate) {
    return new Rome4Version(UUID.randomUUID(), version, lastModifiedDate);
  }

  public static Rome4Version toDomain(UUID id, Integer version, Instant lastModifiedDate) {
    return new Rome4Version(id, version, lastModifiedDate);
  }
}
