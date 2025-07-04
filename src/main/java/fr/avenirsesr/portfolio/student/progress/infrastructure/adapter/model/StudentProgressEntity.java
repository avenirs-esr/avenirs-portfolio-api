package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.student.progress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
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
}
