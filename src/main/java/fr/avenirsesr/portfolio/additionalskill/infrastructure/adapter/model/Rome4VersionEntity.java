package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rome_4_version")
@NoArgsConstructor
@Getter
@Setter
public class Rome4VersionEntity extends AvenirsBaseEntity {

  @Column(nullable = false)
  private Integer version;

  @Column(name = "last_modified_date", nullable = false)
  private Instant lastModifiedDate;

  private Rome4VersionEntity(UUID id, Integer version, Instant lastModifiedDate) {
    setId(id);
    this.version = version;
    this.lastModifiedDate = lastModifiedDate;
  }

  public static Rome4VersionEntity of(UUID id, Integer version, Instant lastModifiedDate) {
    return new Rome4VersionEntity(id, version, lastModifiedDate);
  }
}
