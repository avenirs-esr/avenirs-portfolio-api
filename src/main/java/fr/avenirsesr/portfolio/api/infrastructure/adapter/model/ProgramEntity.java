package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramEntity extends AvenirsBaseEntity {
  @Column(name = "is_apc", nullable = false)
  private boolean isAPC;

  @ManyToOne(optional = false)
  private InstitutionEntity institution;

  @OneToMany(
      mappedBy = "program",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<ProgramTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public ProgramEntity(UUID id, boolean isAPC, InstitutionEntity institution) {
    this.setId(id);
    this.institution = institution;
    this.isAPC = isAPC;
  }
}
