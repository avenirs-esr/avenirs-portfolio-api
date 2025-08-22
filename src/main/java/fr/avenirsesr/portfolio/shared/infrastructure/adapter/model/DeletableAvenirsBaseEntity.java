package fr.avenirsesr.portfolio.shared.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class DeletableAvenirsBaseEntity extends AvenirsBaseEntity {
  @Column(name = "deleted_at")
  private Instant deletedAt;
}
