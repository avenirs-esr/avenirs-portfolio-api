package fr.avenirsesr.portfolio.api.domain.model;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
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

  @Setter(AccessLevel.NONE)
  private final ProgramProgress programProgress;

  private Set<User> users;

  private Set<AMS> amsSet;

  private Cohort(UUID id, String name, String description, ProgramProgress programProgress) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.programProgress = programProgress;
  }

  public static Cohort create(
      UUID id, String name, String description, ProgramProgress programProgress) {
    Cohort cohort = new Cohort(id, name, description, programProgress);
    cohort.setUsers(new HashSet<>());
    cohort.setAmsSet(new HashSet<>());
    return cohort;
  }

  public static Cohort toDomain(
      UUID id,
      String name,
      String description,
      ProgramProgress programProgress,
      Set<User> users,
      Set<AMS> amsSet) {
    Cohort group = new Cohort(id, name, description, programProgress);
    group.setUsers(users);
    group.setAmsSet(amsSet);
    return group;
  }
}
