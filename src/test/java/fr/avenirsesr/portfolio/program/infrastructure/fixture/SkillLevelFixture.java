package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelFixture {

  private UUID id;
  private String name;
  private String description;
  private Skill skill;
  private ELanguage language = ELanguage.FRENCH;
  private Instant createdAt;
  private Instant updatedAt;

  private SkillLevelFixture(
      UUID id,
      String name,
      String description,
      Skill skill,
      ELanguage language,
      Instant createdAt,
      Instant updatedAt) {

    this.id = id;
    this.name = name;
    this.description = description;
    this.skill = skill;
    this.language = language;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static SkillLevelFixture create() {
    Skill skill = SkillFixture.create().toModel();
    return new SkillLevelFixture(
        UUID.randomUUID(),
        "Skill Level " + UUID.randomUUID(),
        "Description for Skill Level " + UUID.randomUUID(),
        skill,
        ELanguage.FRENCH,
        Instant.now(),
        Instant.now());
  }

  public SkillLevelFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillLevelFixture withName(String name) {
    this.name = name;
    return this;
  }

  public SkillLevelFixture withSkill(Skill skill) {
    this.skill = skill;
    return this;
  }

  public List<SkillLevel> withCount(int count) {
    List<SkillLevel> skillLevels = new ArrayList<SkillLevel>();
    for (int i = 0; i < count; i++) {
      skillLevels.add(create().toModel());
    }
    return skillLevels;
  }

  public SkillLevelFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SkillLevelFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public SkillLevelFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillLevelFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SkillLevel toModel() {
    return SkillLevel.toDomain(id, name, description, skill, createdAt, updatedAt);
  }
}
