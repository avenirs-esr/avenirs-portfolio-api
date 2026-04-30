package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import java.time.Instant;
import java.util.Set;

public class FakeTrainingPath {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeTrainingPath.class, SharedDataGenerator.class);
  private final TrainingPathEntity trainingPath;

  private FakeTrainingPath(TrainingPathEntity trainingPath) {
    this.trainingPath = trainingPath;
  }

  public static FakeTrainingPath of(ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    var entity =
        TrainingPathEntity.of(
            dataGenerator.with("id").uuid(), program, skillLevels, Instant.now(), Instant.now());
    return new FakeTrainingPath(entity);
  }

  public TrainingPathEntity toEntity() {
    return trainingPath;
  }
}
