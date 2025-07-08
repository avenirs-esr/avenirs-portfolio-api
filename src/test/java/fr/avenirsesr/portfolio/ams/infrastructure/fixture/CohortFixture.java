package fr.avenirsesr.portfolio.ams.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CohortFixture {

  private UUID id;
  private String name;
  private String description;
  private TrainingPath trainingPath;
  private Set<User> users;
  private Set<AMS> amsSet;

  private CohortFixture() {
    this.id = UUID.randomUUID();
    this.name = "Test Cohort";
    this.description = "Test Cohort Description";
    this.trainingPath = TrainingPathFixture.create().toModel();
    this.users = new HashSet<>();
    this.amsSet = new HashSet<>();
  }

  public static CohortFixture create() {
    return new CohortFixture();
  }

  public CohortFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public CohortFixture withName(String name) {
    this.name = name;
    return this;
  }

  public CohortFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public CohortFixture withTrainingPath(TrainingPath trainingPath) {
    this.trainingPath = trainingPath;
    return this;
  }

  public CohortFixture withUsers(Set<User> users) {
    this.users = users;
    return this;
  }

  public CohortFixture withUsers(int count) {
    Set<User> userSet = new HashSet<>();
    for (int i = 0; i < count; i++) {
      userSet.add(UserFixture.create().toModel());
    }
    this.users = userSet;
    return this;
  }

  public CohortFixture withAmsSet(Set<AMS> amsSet) {
    this.amsSet = amsSet;
    return this;
  }

  public CohortFixture withAmsSet(int count) {
    Set<AMS> amsSet = new HashSet<>();
    for (int i = 0; i < count; i++) {
      amsSet.add(AMSFixture.create().toModel());
    }
    this.amsSet = amsSet;
    return this;
  }

  public List<Cohort> withCount(int count) {
    List<Cohort> cohorts = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      cohorts.add(create().toModel());
    }
    return cohorts;
  }

  public Cohort toModel() {
    return Cohort.toDomain(id, name, description, trainingPath, users, amsSet);
  }
}
