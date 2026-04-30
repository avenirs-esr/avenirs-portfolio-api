package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.temporal.domain.model.enums.EDurationUnit;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "program",
    indexes = {
      @Index(name = "idx_program_institution", columnList = "institution_id"),
      @Index(name = "idx_program_institution_apc", columnList = "institution_id, is_apc")
    })
@NoArgsConstructor
@Getter
@Setter
public class ProgramEntity extends AvenirsBaseEntity {
  @Column(name = "is_apc", nullable = false)
  private boolean isAPC;

  @Column(name = "duration_unit", nullable = true)
  @Enumerated(EnumType.STRING)
  private EDurationUnit durationUnit;

  @Column(name = "duration_count", nullable = true)
  private Integer durationCount;

  @ManyToOne(optional = false)
  private InstitutionEntity institution;

  @OneToMany(
      mappedBy = "program",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<ProgramTranslationEntity> translations = new HashSet<>();

  private ProgramEntity(
      UUID id,
      boolean isAPC,
      InstitutionEntity institution,
      EDurationUnit durationUnit,
      int durationCount,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.institution = institution;
    this.isAPC = isAPC;
    this.durationUnit = durationUnit;
    this.durationCount = durationCount;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ProgramEntity of(
      UUID id,
      boolean isAPC,
      InstitutionEntity institution,
      EDurationUnit durationUnit,
      Integer durationCount,
      Instant createdAt,
      Instant updatedAt) {
    return new ProgramEntity(
        id, isAPC, institution, durationUnit, durationCount, createdAt, updatedAt);
  }
}
