package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.temporal.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.Set;
import java.util.UUID;

public class FakeProgram {
  private static final FakerProvider faker = new FakerProvider().init(FakeProgram.class);
  private final ProgramEntity program;

  private FakeProgram(ProgramEntity program) {
    this.program = program;
  }

  private static EDurationUnit randomDurationUnit() {
    return faker.call("EDurationUnit").options().option(EDurationUnit.values());
  }

  public static FakeProgram of(InstitutionEntity institution) {
    var entity =
        ProgramEntity.of(
            UUID.fromString(faker.call("id").internet().uuid()),
            true,
            institution,
            randomDurationUnit(),
            faker.call("skill-count").number().numberBetween(1, 5));

    entity.setTranslations(
        Set.of(
            ProgramTranslationEntity.of(
                UUID.fromString(faker.call("translation-id").internet().uuid()),
                ELanguage.FRENCH,
                createName(ELanguage.FRENCH),
                entity)));

    return new FakeProgram(entity);
  }

  public FakeProgram isNotAPC() {
    program.setAPC(false);
    return this;
  }

  public FakeProgram addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(program.getTranslations()));
    translations.add(
        ProgramTranslationEntity.of(
            UUID.fromString(faker.call("id").internet().uuid()),
            language,
            createName(language),
            program));

    program.setTranslations(translations);

    return this;
  }

  public ProgramEntity toEntity() {
    return program;
  }

  private static String createName(ELanguage language) {
    return "%s %s - %s [%s]"
        .formatted(
            faker.call("prefix").university().prefix(),
            faker.call("degree").university().degree(),
            faker.call("random number").number().numberBetween(1, 11),
            language.getCode());
  }
}
