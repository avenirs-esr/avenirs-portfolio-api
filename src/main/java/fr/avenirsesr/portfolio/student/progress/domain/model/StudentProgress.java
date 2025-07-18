package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentProgress {
  private final UUID id;
  private final User user;
  private final TrainingPath trainingPath;
  private final SkillLevel skillLevel;
  private ESkillLevelStatus status;

  private StudentProgress(
      UUID id,
      User user,
      TrainingPath trainingPath,
      SkillLevel skillLevel,
      ESkillLevelStatus status) {
    this.id = id;
    this.user = user;
    this.trainingPath = trainingPath;
    this.skillLevel = skillLevel;
    this.status = status;
  }

  public static StudentProgress create(
      UUID id,
      User user,
      TrainingPath trainingPath,
      SkillLevel skillLevel,
      ESkillLevelStatus status) {
    return new StudentProgress(id, user, trainingPath, skillLevel, status);
  }

  public static StudentProgress toDomain(
      UUID id,
      User user,
      TrainingPath trainingPath,
      SkillLevel skillLevel,
      ESkillLevelStatus status) {
    return new StudentProgress(id, user, trainingPath, skillLevel, status);
  }
}
