package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.time.Instant;
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

  private SkillLevel(
      UUID id, Skill skill, String name, String description, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.name = name;
    this.description = description;
    this.skill = skill;
  }

  public static SkillLevel create(UUID id, Skill skill, String name, String description) {
    return new SkillLevel(id, skill, name, description, Instant.now(), Instant.now());
  }

  public static SkillLevel toDomain(
      UUID id, String name, String description, Skill skill, Instant createdAt, Instant updatedAt) {
    return new SkillLevel(id, skill, name, description, createdAt, updatedAt);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }
}
