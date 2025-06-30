package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level")
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelEntity extends PeriodEntity<LocalDate> {

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

  private SkillLevelEntity(
      UUID id,
      ESkillLevelStatus status,
      List<TraceEntity> traces,
      List<AMSEntity> amses,
      SkillEntity skill,
      LocalDate startDate,
      LocalDate endDate) {
    setId(id);
    this.status = status;
    this.traces = traces;
    this.amses = amses;
    this.skill = skill;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static SkillLevelEntity of(
      UUID id,
      ESkillLevelStatus status,
      List<TraceEntity> traces,
      List<AMSEntity> amses,
      SkillEntity skillEntity,
      LocalDate startDate,
      LocalDate endDate) {
    return new SkillLevelEntity(id, status, traces, amses, skillEntity, startDate, endDate);
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(getId());
  }
}
