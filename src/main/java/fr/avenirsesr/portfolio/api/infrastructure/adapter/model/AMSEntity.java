package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AMSEntity extends AvenirsBaseEntity {

  @ManyToOne(optional = false)
  private UserEntity user;

  // TODO: refactor to use the interface Period.
  @Column(name = "start_date", nullable = false)
  private Instant startDate;

  @Column(name = "end_date", nullable = false)
  private Instant endDate;

  @Column
  @Enumerated(EnumType.STRING)
  private EAmsStatus status;

  @ManyToMany
  @JoinTable(
      name = "ams_skill_levels",
      joinColumns = @JoinColumn(name = "ams_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<SkillLevelEntity> skillLevels;

  @ManyToMany
  @JoinTable(
      name = "trace_ams",
      joinColumns = @JoinColumn(name = "ams_id"),
      inverseJoinColumns = @JoinColumn(name = "trace_id"))
  private List<TraceEntity> traces;

  @OneToMany(
      mappedBy = "ams",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<AMSTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  @ManyToMany
  @JoinTable(
      name = "cohort_ams",
      joinColumns = @JoinColumn(name = "ams_id"),
      inverseJoinColumns = @JoinColumn(name = "cohort_id"))
  private Set<CohortEntity> cohorts;

  public AMSEntity(
      UUID id,
      UserEntity user,
      EAmsStatus status,
      Instant startDate,
      Instant endDate,
      Set<SkillLevelEntity> skillLevels,
      Set<CohortEntity> cohorts,
      Set<TraceEntity> traces) {
    setId(id);
    this.user = user;
    this.startDate = startDate;
    this.endDate = endDate;
    this.skillLevels = List.copyOf(skillLevels);
    this.cohorts = Set.copyOf(cohorts == null ? Set.of() : cohorts);
    this.traces = List.copyOf(traces);
    this.status = status;
  }
}
