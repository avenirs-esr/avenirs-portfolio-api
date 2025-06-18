package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.Cohort;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FakeCohort {
  private static final FakerProvider faker = new FakerProvider();
  private final Cohort cohort;

  private FakeCohort(Cohort cohort) {
    this.cohort = cohort;
  }

  public static FakeCohort create() {
    return new FakeCohort(
        Cohort.create(
            UUID.fromString(faker.call().internet().uuid()),
            faker.call().educator().course(),
            faker.call().lorem().sentence(3),
            null));
  }

  public static FakeCohort of(ProgramProgress programProgress) {
    return new FakeCohort(
        Cohort.create(
            UUID.fromString(faker.call().internet().uuid()),
            faker.call().educator().course(),
            faker.call().lorem().sentence(3),
            programProgress));
  }

  public FakeCohort withProgramProgress(ProgramProgress programProgress) {
    return new FakeCohort(
        Cohort.create(
            cohort.getId(),
            cohort.getName(),
            cohort.getDescription(),
            programProgress));
  }

  public FakeCohort withUsers(Set<User> users) {
    cohort.setUsers(users);
    return this;
  }

  public FakeCohort withUser(User user) {
    Set<User> users = new HashSet<>(cohort.getUsers());
    users.add(user);
    cohort.setUsers(users);
    return this;
  }

  public FakeCohort withAMS(AMS ams) {
    Set<AMS> amsSet = new HashSet<>(cohort.getAmsSet());
    amsSet.add(ams);
    cohort.setAmsSet(amsSet);
    return this;
  }

  public FakeCohort withAMSSet(Set<AMS> amsSet) {
    cohort.setAmsSet(amsSet);
    return this;
  }

  public Cohort toModel() {
    return cohort;
  }
}
