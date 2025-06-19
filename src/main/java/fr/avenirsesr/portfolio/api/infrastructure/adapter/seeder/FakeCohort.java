package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Cohort;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.CohortMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.CohortEntity;

import java.util.HashSet;
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
