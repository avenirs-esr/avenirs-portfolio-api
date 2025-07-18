package fr.avenirsesr.portfolio.program.domain.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skill {
  private final UUID id;
  private final String name;

  private Skill(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Skill create(UUID id, String name) {
    return new Skill(id, name);
  }

  public static Skill toDomain(UUID id, String name) {
    return new Skill(id, name);
  }

  @Override
  public String toString() {
    return "Skill[%s]".formatted(id);
  }
}
