package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
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
  @JoinColumn(name = "training_path_id")
  private TrainingPathEntity trainingPath;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "student_progress_id")
  private List<SkillLevelProgressEntity> skillLevels;

  private StudentProgressEntity(
      UUID id,
      UserEntity student,
      TrainingPathEntity trainingPath,
      List<SkillLevelProgressEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.student = student;
    this.trainingPath = trainingPath;
    this.skillLevels = skillLevels;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static StudentProgressEntity of(
      UUID id,
      UserEntity student,
      TrainingPathEntity trainingPath,
      List<SkillLevelProgressEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    return new StudentProgressEntity(id, student, trainingPath, skillLevels, createdAt, updatedAt);
  }
}
