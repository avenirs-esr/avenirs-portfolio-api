package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "association", uniqueConstraints = @UniqueConstraint(columnNames = {"id1", "id2"}))
@NoArgsConstructor
@Getter
@Setter
public class AssociationEntity extends AvenirsBaseEntity {
  @Column(nullable = false)
  private UUID id1;

  @Column(nullable = false)
  private UUID id2;

  @Column(nullable = false, name = "association_type")
  @Enumerated(EnumType.STRING)
  private EAssociationType associationType;

  private AssociationEntity(
      UUID id,
      UUID id1,
      UUID id2,
      EAssociationType associationType,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.id1 = id1;
    this.id2 = id2;
    this.associationType = associationType;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static AssociationEntity of(
      UUID id,
      UUID id1,
      UUID id2,
      EAssociationType associationType,
      Instant createdAt,
      Instant updatedAt) {
    return new AssociationEntity(id, id1, id2, associationType, createdAt, updatedAt);
  }
}
