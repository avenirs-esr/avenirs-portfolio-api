package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import java.time.Instant;
import java.util.*;

public class SkillFixture {

  private UUID id;
  private String name;
  private ELanguage language = ELanguage.FALLBACK;
  private Instant createdAt;
  private Instant updatedAt;

  private SkillFixture(
      UUID id, String name, ELanguage language, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.language = language;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static SkillFixture create() {
    return new SkillFixture(
        UUID.randomUUID(), "Default Skill", ELanguage.FALLBACK, Instant.now(), Instant.now());
  }

  public SkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillFixture withName(String name) {
    this.name = name;
    return this;
  }

  public SkillFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public SkillFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Skill toModel() {
    return Skill.toDomain(id, name, createdAt, updatedAt);
  }
}
