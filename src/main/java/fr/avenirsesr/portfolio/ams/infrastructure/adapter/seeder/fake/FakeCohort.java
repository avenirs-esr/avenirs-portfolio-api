package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.Set;
import java.util.UUID;

public class FakeCohort {
  private static final FakerProvider faker = new FakerProvider();
  private final Cohort cohort;

  private FakeCohort(Cohort cohort) {
    this.cohort = cohort;
  }

  public static FakeCohort of(ProgramProgress programProgress, Set<User> users) {
    final Cohort cohort =
        Cohort.create(
            UUID.fromString(faker.call().internet().uuid()),
            faker.call().educator().course(),
            faker.call().lorem().sentence(),
            programProgress);
    cohort.setUsers(users);
    return new FakeCohort(cohort);
  }

  public Cohort toModel() {
    return cohort;
  }
}
