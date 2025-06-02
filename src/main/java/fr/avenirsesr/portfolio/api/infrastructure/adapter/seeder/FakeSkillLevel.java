package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import java.util.UUID;

public class FakeSkillLevel {
  private static final FakerProvider faker = new FakerProvider();
  private final SkillLevel skillLevel;

  private FakeSkillLevel(SkillLevel skillLevel) {
    this.skillLevel = skillLevel;
  }

  public static FakeSkillLevel create() {
    return new FakeSkillLevel(
        SkillLevel.create(
            UUID.fromString(faker.call().internet().uuid()),
            "Niv. %s".formatted(faker.call().lorem().character())));
  }

  public FakeSkillLevel withStatus(ESkillLevelStatus status) {
    skillLevel.setStatus(status);
    return this;
  }

  public SkillLevel toModel() {
    return skillLevel;
  }
}
