package fr.avenirsesr.portfolio.student.progress.infrastructure.fixture;

import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.UUID;

public class StudentProgressFixture {
  private UUID id;
  private User user;
  private TrainingPath trainingPath;
  private SkillLevel skillLevel;
  private ESkillLevelStatus status;

  private StudentProgressFixture() {
    this.id = UUID.randomUUID();
    this.user = UserFixture.create().toModel();
    this.trainingPath = TrainingPathFixture.create().toModel();
    this.skillLevel = SkillLevelFixture.create().toModel();
    this.status = ESkillLevelStatus.NOT_STARTED;
  }

  public static StudentProgressFixture create() {
    return new StudentProgressFixture();
  }

  public StudentProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public StudentProgressFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public StudentProgressFixture withTrainingPath(TrainingPath trainingPath) {
    this.trainingPath = trainingPath;
    return this;
  }

  public StudentProgressFixture withSkillLevel(SkillLevel skillLevel) {
    this.skillLevel = skillLevel;
    return this;
  }

  public StudentProgressFixture withStatus(ESkillLevelStatus status) {
    this.status = status;
    return this;
  }

  public StudentProgress toModel() {
    return StudentProgress.toDomain(id, user, trainingPath, skillLevel, status);
  }
}
