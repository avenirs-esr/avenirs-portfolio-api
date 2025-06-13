package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
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

  private final ELanguage language;

  private Skill(UUID id, String name, Set<SkillLevel> skillLevels, ELanguage language) {
    this.id = id;
    this.name = name;
    this.skillLevels = skillLevels;
    this.language = language;
  }

  public static Skill create(
      UUID id, String name, Set<SkillLevel> skillLevels, ELanguage language) {
    return new Skill(id, name, skillLevels, language);
  }

  public static Skill toDomain(
      UUID id, String name, Set<SkillLevel> skillLevels, ELanguage language) {
    return new Skill(id, name, skillLevels, language);
  }

  @Override
  public String toString() {
    return "Skill[%s]".formatted(id);
  }
}
