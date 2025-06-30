package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.Set;
import java.util.UUID;

public class FakeCohort {
  private static final FakerProvider faker = new FakerProvider();
  private final CohortEntity cohort;

  private FakeCohort(CohortEntity cohort) {
    this.cohort = cohort;
  }

  public static FakeCohort of(ProgramProgressEntity programProgress, Set<UserEntity> users) {
    final CohortEntity cohort =
        CohortEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            faker.call().educator().course(),
            faker.call().lorem().sentence(),
            users,
            programProgress,
            Set.of());
    return new FakeCohort(cohort);
  }

  public CohortEntity toEntity() {
    return cohort;
  }
}
