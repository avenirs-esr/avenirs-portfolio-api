package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "student_progress",
    indexes = {
      @Index(name = "idx_sp_student", columnList = "student_id"),
      @Index(name = "idx_sp_student_training_path", columnList = "student_id, training_path_id")
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentProgressEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(optional = false)
  private StudentEntity student;

  @ManyToOne(optional = false)
  @JoinColumn(name = "training_path_id")
  private TrainingPathEntity trainingPath;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "student_progress_id")
  private List<SkillLevelProgressEntity> skillLevels;

  private StudentProgressEntity(
      UUID id,
      StudentEntity student,
      TrainingPathEntity trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgressEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.student = student;
    this.trainingPath = trainingPath;
    this.startDate = startDate;
    this.endDate = endDate;
    this.skillLevels = skillLevels;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static StudentProgressEntity of(
      UUID id,
      StudentEntity student,
      TrainingPathEntity trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgressEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    return new StudentProgressEntity(
        id, student, trainingPath, startDate, endDate, skillLevels, createdAt, updatedAt);
  }
}
