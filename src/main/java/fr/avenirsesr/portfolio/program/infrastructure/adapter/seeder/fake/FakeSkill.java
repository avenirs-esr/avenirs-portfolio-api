package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;
import java.util.List;
import java.util.Set;

public class FakeSkill {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeSkill.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<ProgramDataGenerator> programDataGenerator =
      new DataGeneratorProvider<ProgramDataGenerator>()
          .init(FakeSkill.class, ProgramDataGenerator.class);
  private final SkillEntity skill;

  private FakeSkill(SkillEntity skill) {
    this.skill = skill;
  }

  public static FakeSkill of(List<SkillLevelEntity> skillLevels) {
    var entity = SkillEntity.of(dataGenerator.with("id").uuid());

    skillLevels.forEach(skillLevel -> skillLevel.setSkill(entity));

    entity.setTranslations(
        Set.of(
            SkillTranslationEntity.of(
                dataGenerator.with("FALLBACK-translation-id", ELanguage.FALLBACK).uuid(),
                ELanguage.FALLBACK,
                programDataGenerator.with("word").skill(),
                entity)));
    return new FakeSkill(entity);
  }

  public FakeSkill addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(skill.getTranslations()));
    translations.add(
        SkillTranslationEntity.of(
            dataGenerator.with("translation-id", language).uuid(),
            language,
            programDataGenerator.with("translation word", language).skill(),
            skill));

    skill.setTranslations(translations);

    return this;
  }

  public SkillEntity toEntity() {
    return skill;
  }
}
