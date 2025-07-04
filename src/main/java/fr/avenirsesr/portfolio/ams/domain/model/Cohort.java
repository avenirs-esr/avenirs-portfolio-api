package fr.avenirsesr.portfolio.ams.domain.model;

import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cohort {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  private String name;

  private String description;

  private TrainingPath trainingPath;

  private Set<User> users;

  private Set<AMS> amsSet;

  private Cohort(UUID id, String name, String description, TrainingPath trainingPath) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.trainingPath = trainingPath;
  }

  public static Cohort create(UUID id, String name, String description, TrainingPath trainingPath) {
    Cohort cohort = new Cohort(id, name, description, trainingPath);
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
      Set<AMS> amsSet) {
    Cohort group = new Cohort(id, name, description, trainingPath);
    group.setUsers(users);
    group.setAmsSet(amsSet);
    return group;
  }
}
