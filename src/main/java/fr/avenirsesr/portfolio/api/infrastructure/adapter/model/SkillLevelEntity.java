package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelEntity {
  @Id private UUID id;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  @ManyToMany
  @JoinTable(
      name = "trace_skill_levels",
      joinColumns = @JoinColumn(name = "skill_level_id"),
      inverseJoinColumns = @JoinColumn(name = "trace_id"))
  private List<TraceEntity> traces;

  @ManyToMany
  @JoinTable(
      name = "ams_skill_levels",
      joinColumns = @JoinColumn(name = "skill_level_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id")
  private SkillEntity skill;

  @OneToMany(
      mappedBy = "skillLevel",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SkillLevelTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public SkillLevelEntity(
      UUID id,
      ESkillLevelStatus status,
      List<TraceEntity> traces,
      List<AMSEntity> amses,
      SkillEntity skill) {
    this.id = id;
    this.status = status;
    this.traces = traces;
    this.amses = amses;
    this.skill = skill;
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(id);
  }
}
