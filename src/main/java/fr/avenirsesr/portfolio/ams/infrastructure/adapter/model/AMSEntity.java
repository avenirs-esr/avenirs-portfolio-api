package fr.avenirsesr.portfolio.ams.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ams")
@NoArgsConstructor
@Getter
@Setter
public class AMSEntity extends PeriodEntity<Instant> {

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EAmsStatus status;

  @ManyToMany
  @JoinTable(
      name = "ams_skill_level",
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

  private AMSEntity(
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
    setStartDate(startDate);
    setEndDate(endDate);
    this.skillLevels = List.copyOf(skillLevels);
    this.cohorts = Set.copyOf(cohorts == null ? Set.of() : cohorts);
    this.traces = List.copyOf(traces);
    this.status = status;
  }

  public static AMSEntity of(
      UUID id,
      UserEntity user,
      EAmsStatus status,
      Instant startDate,
      Instant endDate,
      Set<SkillLevelEntity> skillLevels,
      Set<CohortEntity> cohorts,
      Set<TraceEntity> traces) {
    return new AMSEntity(id, user, status, startDate, endDate, skillLevels, cohorts, traces);
  }
}
