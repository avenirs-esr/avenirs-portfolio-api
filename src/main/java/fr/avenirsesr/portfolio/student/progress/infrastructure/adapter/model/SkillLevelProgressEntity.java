package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level_progress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelProgressEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private UserEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_level_id")
  private SkillLevelEntity skillLevel;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  @ManyToMany(mappedBy = "skillLevels")
  private List<TraceEntity> traces;

  @ManyToMany(mappedBy = "skillLevels")
  private List<AMSEntity> amses;

  private SkillLevelProgressEntity(
      UUID id,
      UserEntity student,
      SkillLevelEntity skillLevelEntity,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      List<TraceEntity> traces,
      List<AMSEntity> amses) {
    setId(id);
    this.student = student;
    this.skillLevel = skillLevelEntity;
    this.status = status;
    this.traces = traces;
    this.amses = amses;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static SkillLevelProgressEntity of(
      UUID id,
      UserEntity student,
      SkillLevelEntity skillLevelEntity,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      List<TraceEntity> traces,
      List<AMSEntity> amses) {
    return new SkillLevelProgressEntity(
        id, student, skillLevelEntity, status, startDate, endDate, traces, amses);
  }

  @Override
  public String toString() {
    return "SkillLevelProgressEntity[%s]".formatted(getId());
  }
}
