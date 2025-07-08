package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.Set;
import java.util.UUID;

public class FakeTrainingPath {
  private static final FakerProvider faker = new FakerProvider();
  private final TrainingPathEntity trainingPath;

  private FakeTrainingPath(TrainingPathEntity trainingPath) {
    this.trainingPath = trainingPath;
  }

  public static FakeTrainingPath of(ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    return new FakeTrainingPath(
        TrainingPathEntity.of(
            UUID.fromString(faker.call().internet().uuid()), program, skillLevels));
  }

  public TrainingPathEntity toEntity() {
    return trainingPath;
  }
}
