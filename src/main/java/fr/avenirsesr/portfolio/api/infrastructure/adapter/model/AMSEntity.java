package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
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

  @ManyToMany(mappedBy = "amsEntities")
  private Set<CohortEntity> cohorts;

  public AMSEntity(
      UUID id,
      UserEntity user,
      Instant startDate,
      Instant endDate,
      Set<SkillLevelEntity> skillLevels,
      Set<CohortEntity> cohorts) {
    setId(id);
    this.user = user;
    this.startDate = startDate;
    this.endDate = endDate;
    this.skillLevels = List.copyOf(skillLevels);
    this.cohorts = Set.copyOf(cohorts == null ? Set.of() : cohorts);
    this.traces = List.of();
  }
}
