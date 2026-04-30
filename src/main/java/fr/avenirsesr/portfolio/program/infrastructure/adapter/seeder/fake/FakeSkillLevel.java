package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelTranslationEntity;
import java.time.Instant;
import java.util.Set;

public class FakeSkillLevel {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeSkillLevel.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<ProgramDataGenerator> programDataGenerator =
      new DataGeneratorProvider<ProgramDataGenerator>()
          .init(FakeSkillLevel.class, ProgramDataGenerator.class);
  private final SkillLevelEntity skillLevel;

  private FakeSkillLevel(SkillLevelEntity skillLevel) {
    this.skillLevel = skillLevel;
  }

  public static FakeSkillLevel create() {
    SkillLevelEntity entity =
        SkillLevelEntity.of(dataGenerator.with("id").uuid(), null, Instant.now(), Instant.now());

    entity.setTranslations(
        Set.of(
            SkillLevelTranslationEntity.of(
                dataGenerator.with("fallback-translation-id").uuid(),
                ELanguage.FALLBACK,
                programDataGenerator.with("character", ELanguage.FALLBACK).skillLevelName(),
                programDataGenerator.with("sentence", ELanguage.FALLBACK).skillLevelDescription(),
                entity)));

    return new FakeSkillLevel(entity);
  }

  public FakeSkillLevel addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(skillLevel.getTranslations()));
    translations.add(
        SkillLevelTranslationEntity.of(
            dataGenerator.with("translation-id").uuid(),
            language,
            programDataGenerator.with("translation-character", language).skillLevelName(),
            programDataGenerator.with("translation-sentence", language).skillLevelDescription(),
            skillLevel));

    skillLevel.setTranslations(translations);

    return this;
  }

  public SkillLevelEntity toEntity() {
    return skillLevel;
  }
}
