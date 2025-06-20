package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
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
public class SkillLevelEntity extends PeriodEntity<LocalDate> {
  @Id private UUID id;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  @ManyToMany(mappedBy = "skillLevels")
  private List<TraceEntity> traces;

  @ManyToMany(mappedBy = "skillLevels")
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
      SkillEntity skill,
      LocalDate startDate,
      LocalDate endDate) {
    this.id = id;
    this.status = status;
    this.traces = traces;
    this.amses = amses;
    this.skill = skill;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(id);
  }
}
