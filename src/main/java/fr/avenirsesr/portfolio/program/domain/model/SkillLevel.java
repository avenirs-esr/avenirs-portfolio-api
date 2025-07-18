package fr.avenirsesr.portfolio.program.domain.model;

import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillLevel {
  private final UUID id;
  private final String name;
  private final Skill skill;

  @Getter(AccessLevel.NONE)
  private final String description;

  private SkillLevel(UUID id, Skill skill, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.skill = skill;
  }

  public static SkillLevel create(UUID id, Skill skill, String name, String description) {
    return new SkillLevel(id, skill, name, description);
  }

  public static SkillLevel toDomain(UUID id, String name, String description, Skill skill) {
    return new SkillLevel(id, skill, name, description);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  @Override
  public String toString() {
    return "SkillLevel[%s]".formatted(id);
  }
}
