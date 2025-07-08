package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_progress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private UserEntity student;

  @ManyToOne(optional = false)
  private TrainingPathEntity trainingPath;

  @ManyToOne(optional = false)
  private SkillLevelEntity skillLevel;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  private StudentProgressEntity(
      UUID id,
      UserEntity student,
      TrainingPathEntity trainingPath,
      SkillLevelEntity skillLevel,
      ESkillLevelStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.student = student;
    this.trainingPath = trainingPath;
    this.skillLevel = skillLevel;
    this.status = status;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static StudentProgressEntity of(
      UUID id,
      UserEntity student,
      TrainingPathEntity trainingPath,
      SkillLevelEntity skillLevel,
      ESkillLevelStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new StudentProgressEntity(
        id, student, trainingPath, skillLevel, status, createdAt, updatedAt);
  }
}
