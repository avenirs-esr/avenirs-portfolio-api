package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillLevelTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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
        SkillLevelEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            null,
            List.of(),
            List.of(),
            null,
            null,
            null);

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

    return new FakeSkillLevel(entity).withStatus(ESkillLevelStatus.NOT_STARTED);
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

  public FakeSkillLevel withStatus(ESkillLevelStatus status) {
    LocalDate pastStartDate = LocalDate.now().minus(Period.ofYears(2));
    LocalDate pastEndDate = LocalDate.now().minus(Period.ofYears(1));
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    skillLevel.setStatus(status);
    switch (status) {
      case VALIDATED, FAILED -> {
        skillLevel.setStartDate(pastStartDate);
        skillLevel.setEndDate(pastEndDate);
      }
      case UNDER_ACQUISITION, UNDER_REVIEW -> {
        skillLevel.setStartDate(pastStartDate);
        skillLevel.setEndDate(futureEndDate);
      }
      case TO_BE_EVALUATED, NOT_STARTED -> {
        skillLevel.setStartDate(futureStartDate);
        skillLevel.setEndDate(futureEndDate);
      }
    }
    return this;
  }

  public SkillLevelEntity toEntity() {
    return skillLevel;
  }
}
