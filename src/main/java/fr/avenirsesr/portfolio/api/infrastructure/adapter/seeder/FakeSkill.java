package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import java.util.Set;
import java.util.UUID;

public class FakeSkill {
  private static final FakerProvider faker = new FakerProvider();
  private final Skill skill;

  private FakeSkill(Skill skill) {
    this.skill = skill;
  }

  public static FakeSkill of(Set<SkillLevel> skillLevels) {
    return new FakeSkill(
        Skill.create(
            UUID.fromString(faker.call().internet().uuid()),
            "Skill %s".formatted(faker.call().lorem().word()),
            skillLevels));
  }

  public Skill toModel() {
    return skill;
  }
}
