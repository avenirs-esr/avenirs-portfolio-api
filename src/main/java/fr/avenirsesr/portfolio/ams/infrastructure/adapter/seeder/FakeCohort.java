package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.CohortDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.Set;

public class FakeCohort {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeCohort.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<CohortDataGenerator> cohortGenerator =
      new DataGeneratorProvider<CohortDataGenerator>()
          .init(FakeCohort.class, CohortDataGenerator.class);
  private final CohortEntity cohort;

  private FakeCohort(CohortEntity cohort) {
    this.cohort = cohort;
  }

  public static FakeCohort of(TrainingPathEntity trainingPath, Set<UserEntity> users) {
    final CohortEntity cohort =
        CohortEntity.of(
            dataGenerator.with("id").uuid(),
            cohortGenerator.with("course").name(),
            cohortGenerator.with("sentence").description(),
            users,
            trainingPath,
            Set.of());
    return new FakeCohort(cohort);
  }

  public CohortEntity toEntity() {
    return cohort;
  }
}
