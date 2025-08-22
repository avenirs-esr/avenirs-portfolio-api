package fr.avenirsesr.portfolio.ams.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cohort extends AvenirsBaseModel {
  private String name;
  private String description;
  private TrainingPath trainingPath;
  private Set<User> users;
  private Set<AMS> amsSet;

  private Cohort(
      UUID id,
      String name,
      String description,
      TrainingPath trainingPath,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.name = name;
    this.description = description;
    this.trainingPath = trainingPath;
  }

  public static Cohort create(UUID id, String name, String description, TrainingPath trainingPath) {
    Cohort cohort = new Cohort(id, name, description, trainingPath, Instant.now(), Instant.now());
    cohort.setUsers(new HashSet<>());
    cohort.setAmsSet(new HashSet<>());
    return cohort;
  }

  public static Cohort toDomain(
      UUID id,
      String name,
      String description,
      TrainingPath trainingPath,
      Set<User> users,
      Set<AMS> amsSet,
      Instant createdAt,
      Instant updatedAt) {
    Cohort group = new Cohort(id, name, description, trainingPath, createdAt, updatedAt);
    group.setUsers(users);
    group.setAmsSet(amsSet);
    return group;
  }
}
