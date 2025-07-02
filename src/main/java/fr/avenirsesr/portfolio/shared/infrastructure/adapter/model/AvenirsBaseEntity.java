package fr.avenirsesr.portfolio.shared.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AvenirsBaseEntity {

  @Id private UUID id;

  @Column(
      name = "created_at",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  private Instant createdAt;

  @Column(
      name = "updated_at",
      insertable = false,
      columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  private Instant updatedAt;
}
