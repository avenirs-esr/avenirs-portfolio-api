package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import java.util.Set;
import net.datafaker.Faker;

public class FakeSkill {
  private static final Faker faker = new Faker();
  private final Skill skill;

  private FakeSkill(Skill skill) {
    this.skill = skill;
  }

  public static FakeSkill of(Set<SkillLevel> skillLevels) {
    var skill = Skill.create("Skill %s".formatted(faker.lorem().word()), Set.of());
    skillLevels.forEach(
        level -> {
          level.setSkill(skill);
        });
    skill.setSkillLevels(skillLevels);
    return new FakeSkill(skill);
  }

  public Skill toModel() {
    return skill;
  }
}
