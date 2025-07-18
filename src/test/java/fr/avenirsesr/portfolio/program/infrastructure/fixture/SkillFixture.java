package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.*;

public class SkillFixture {

  private UUID id;
  private String name;
  private ELanguage language = ELanguage.FALLBACK;

  private SkillFixture() {
    var skillLevelEntity = FakeSkillLevel.create().toEntity();
    var skillEntity = FakeSkill.of(List.of(skillLevelEntity)).toEntity();
    skillLevelEntity.setSkill(skillEntity);
    Skill base = SkillMapper.toDomain(skillEntity);

    this.id = base.getId();
    this.name = base.getName();
  }

  public static SkillFixture create() {
    return new SkillFixture();
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

  public Skill toModel() {
    return Skill.toDomain(id, name);
  }
}
