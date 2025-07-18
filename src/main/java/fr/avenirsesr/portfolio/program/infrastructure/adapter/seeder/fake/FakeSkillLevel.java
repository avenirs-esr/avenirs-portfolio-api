package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.Set;
import java.util.UUID;

public class FakeSkillLevel {
  private static final FakerProvider faker = new FakerProvider();
  private final SkillLevelEntity skillLevel;

  private FakeSkillLevel(SkillLevelEntity skillLevel) {
    this.skillLevel = skillLevel;
  }

  public static FakeSkillLevel create() {
    SkillLevelEntity entity =
        SkillLevelEntity.of(UUID.fromString(faker.call().internet().uuid()), null);

    entity.setTranslations(
        Set.of(
            SkillLevelTranslationEntity.of(
                UUID.fromString(faker.call().internet().uuid()),
                ELanguage.FALLBACK,
                "Niv. %s - [%s]"
                    .formatted(faker.call().lorem().character(), ELanguage.FALLBACK.getCode()),
                "%s - [%s]"
                    .formatted(faker.call().lorem().sentence(), ELanguage.FALLBACK.getCode()),
                entity)));

    return new FakeSkillLevel(entity);
  }

  public FakeSkillLevel addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(skillLevel.getTranslations()));
    translations.add(
        SkillLevelTranslationEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            language,
            "Niv. %s - [%s]".formatted(faker.call().lorem().character(), language.getCode()),
            "%s - [%s]".formatted(faker.call().lorem().sentence(), language.getCode()),
            skillLevel));

    skillLevel.setTranslations(translations);

    return this;
  }

  public SkillLevelEntity toEntity() {
    return skillLevel;
  }
}
