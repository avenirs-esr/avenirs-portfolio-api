package fr.avenirsesr.portfolio.api.domain.model;

import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skill {
  private final UUID id;
  private final String name;
  private Set<SkillLevel> skillLevels;
  private ProgramProgress programProgress;

  private Skill(UUID id, String name, Set<SkillLevel> skillLevels) {
    this.id = id;
    this.name = name;
    this.skillLevels = skillLevels;
  }

  public static Skill create(String name, Set<SkillLevel> skillLevels) {
    return new Skill(UUID.randomUUID(), name, skillLevels);
  }

  public static Skill toDomain(UUID id, String name, Set<SkillLevel> skillLevels) {
    return new Skill(id, name, skillLevels);
  }

  @Override
  public String toString() {
    return "Skill[%s]".formatted(id);
  }
}
