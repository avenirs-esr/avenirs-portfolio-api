package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FakeSkill {
  private static final FakerProvider faker = new FakerProvider();
  private final SkillEntity skill;

  private FakeSkill(SkillEntity skill) {
    this.skill = skill;
  }

  public static FakeSkill of(List<SkillLevelEntity> skillLevels) {
    var entity = SkillEntity.of(UUID.fromString(faker.call().internet().uuid()));

    skillLevels.forEach(skillLevel -> skillLevel.setSkill(entity));

    entity.setTranslations(
        Set.of(
            SkillTranslationEntity.of(
                UUID.fromString(faker.call().internet().uuid()),
                ELanguage.FALLBACK,
                "Skill %s - [%s]"
                    .formatted(faker.call().lorem().word(), ELanguage.FALLBACK.getCode()),
                entity)));
    return new FakeSkill(entity);
  }

  public FakeSkill addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(skill.getTranslations()));
    translations.add(
        SkillTranslationEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            language,
            "Skill %s - [%s]".formatted(faker.call().lorem().word(), language.getCode()),
            skill));

    skill.setTranslations(translations);

    return this;
  }

  public SkillEntity toEntity() {
    return skill;
  }
}
