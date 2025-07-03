package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
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
  private int durationCount;

  @ManyToOne(optional = false)
  private InstitutionEntity institution;

  @OneToMany(
      mappedBy = "program",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<ProgramTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  private ProgramEntity(
      UUID id,
      boolean isAPC,
      InstitutionEntity institution,
      EDurationUnit durationUnit,
      int durationCount) {
    this.setId(id);
    this.institution = institution;
    this.isAPC = isAPC;
    this.durationUnit = durationUnit;
    this.durationCount = durationCount;
  }

  public static ProgramEntity of(
      UUID id,
      boolean isAPC,
      InstitutionEntity institution,
      EDurationUnit durationUnit,
      int durationCount) {
    return new ProgramEntity(id, isAPC, institution, durationUnit, durationCount);
  }
}
