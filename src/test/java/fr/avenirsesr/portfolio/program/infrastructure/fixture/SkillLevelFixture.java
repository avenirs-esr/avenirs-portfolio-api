package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkillLevel;
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

  private SkillLevelFixture() {
    var skillLevelEntity = FakeSkillLevel.create().toEntity();
    var skillEntity = FakeSkill.of(List.of(skillLevelEntity)).toEntity();
    skillLevelEntity.setSkill(skillEntity);
    SkillLevel base = SkillLevelMapper.toDomain(skillLevelEntity);

    this.id = base.getId();
    this.name = base.getName();
    this.description = base.getDescription().orElse(null);
    this.skill = base.getSkill();
    this.createdAt = base.getCreatedAt();
    this.updatedAt = base.getUpdatedAt();
  }

  public static SkillLevelFixture create() {
    return new SkillLevelFixture();
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
