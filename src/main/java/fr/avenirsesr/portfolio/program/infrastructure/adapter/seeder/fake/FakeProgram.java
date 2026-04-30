package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.temporal.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramTranslationEntity;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class FakeProgram {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeProgram.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<ProgramDataGenerator> programDataGenerator =
      new DataGeneratorProvider<ProgramDataGenerator>()
          .init(FakeProgram.class, ProgramDataGenerator.class);
  private final ProgramEntity program;

  private FakeProgram(ProgramEntity program) {
    this.program = program;
  }

  private static EDurationUnit randomDurationUnit() {
    return dataGenerator.with("EDurationUnit").pickIn(List.of(EDurationUnit.values()));
  }

  public static FakeProgram of(InstitutionEntity institution) {
    var entity =
        ProgramEntity.of(
            dataGenerator.with("id").uuid(),
            true,
            institution,
            randomDurationUnit(),
            dataGenerator.with("skill-count").number(1, 5),
            Instant.now(),
            Instant.now());

    entity.setTranslations(
        Set.of(
            ProgramTranslationEntity.of(
                dataGenerator.with("translation-id", ELanguage.FRENCH).uuid(),
                ELanguage.FRENCH,
                programDataGenerator.with("program-default", ELanguage.FRENCH).program(),
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
            dataGenerator.with("translation-id", language).uuid(),
            language,
            programDataGenerator.with("program-translated", language).program(),
            program));

    program.setTranslations(translations);

    return this;
  }

  public ProgramEntity toEntity() {
    return program;
  }
}
