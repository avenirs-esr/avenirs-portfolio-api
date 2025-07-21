package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillLevel extends AvenirsBaseModel {
  private final String name;
  private final Skill skill;

  @Getter(AccessLevel.NONE)
  private final String description;

  private SkillLevel(UUID id, Skill skill, String name, String description) {
    super(id);
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
}
