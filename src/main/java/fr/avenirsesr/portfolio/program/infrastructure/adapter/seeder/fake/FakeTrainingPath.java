package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.DataGeneratorProvider;
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
    var entity = TrainingPathEntity.of(dataGenerator.with("id").uuid(), program, skillLevels);
    return new FakeTrainingPath(entity);
  }

  public TrainingPathEntity toEntity() {
    return trainingPath;
  }
}
