package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Program {
  private final UUID id;
  private final Institution institution;
  private final String name;
  private boolean isAPC;

  private Program(UUID id, Institution institution, String name, boolean isAPC) {
    this.id = id;
    this.institution = institution;
    this.name = name;
    this.isAPC = isAPC;
  }

  public static Program create(Institution institution, String name, boolean isAPC) {
    return new Program(UUID.randomUUID(), institution, name, isAPC);
  }

  public static Program toDomain(UUID id, Institution institution, String name, boolean isAPC) {
    return new Program(id, institution, name, isAPC);
  }
}
