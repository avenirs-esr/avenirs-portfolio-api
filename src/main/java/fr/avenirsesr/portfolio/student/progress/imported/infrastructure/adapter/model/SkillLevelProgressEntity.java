package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "skill_level_progress",
    indexes = {
      @Index(name = "idx_slp_student", columnList = "student_id"),
      @Index(name = "idx_slp_student_skill_level", columnList = "student_id, skill_level_id")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelProgressEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_level_id")
  private SkillLevelEntity skillLevel;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  private SkillLevelProgressEntity(
      UUID id,
      StudentEntity student,
      SkillLevelEntity skillLevelEntity,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    this.student = student;
    this.skillLevel = skillLevelEntity;
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static SkillLevelProgressEntity of(
      UUID id,
      StudentEntity student,
      SkillLevelEntity skillLevelEntity,
      ESkillLevelStatus status,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    return new SkillLevelProgressEntity(
        id, student, skillLevelEntity, status, startDate, endDate, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "SkillLevelProgressEntity[%s]".formatted(getId());
  }
}
