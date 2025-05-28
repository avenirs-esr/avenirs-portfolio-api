package fr.avenirsesr.portfolio.api.domain.model;

import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Skill {
  private final UUID id;
  private final String name;
  private final Set<SkillLevel> skillLevels;

  private Skill(UUID id, String name, Set<SkillLevel> skillLevels) {
    this.id = id;
    this.name = name;
    this.skillLevels = skillLevels;
  }

  public static Skill create(UUID id, String name, Set<SkillLevel> skillLevels) {
    return new Skill(id, name, skillLevels);
  }

  public static Skill toDomain(UUID id, String name, Set<SkillLevel> skillLevels) {
    return new Skill(id, name, skillLevels);
  }
}
