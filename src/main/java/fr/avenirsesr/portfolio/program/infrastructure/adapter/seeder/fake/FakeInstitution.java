package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.DataGeneratorProvider;
import java.util.Set;

public class FakeInstitution {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeInstitution.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<ProgramDataGenerator> programDataGenerator =
      new DataGeneratorProvider<ProgramDataGenerator>()
          .init(FakeInstitution.class, ProgramDataGenerator.class);
  private final InstitutionEntity institution;

  private FakeInstitution(InstitutionEntity institution) {
    this.institution = institution;
  }

  public static FakeInstitution create() {
    var entity = InstitutionEntity.of(dataGenerator.with("id").uuid(), Set.of());
    var fakeInstitution = new FakeInstitution(entity);

    entity.setTranslations(
        Set.of(
            InstitutionTranslationEntity.of(
                dataGenerator.with("fallback-translation-id", ELanguage.FRENCH).uuid(),
                ELanguage.FRENCH,
                programDataGenerator.with("university").university(),
                entity)));

    return fakeInstitution;
  }

  public void addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(institution.getTranslations()));

    translations.add(
        InstitutionTranslationEntity.of(
            dataGenerator.with("translation-id", language).uuid(),
            language,
            programDataGenerator.with("university-translation", language).university(),
            institution));

    institution.setTranslations(translations);
  }

  public FakeInstitution withEnabledFiled(Set<EPortfolioType> enabledFields) {
    institution.setEnabledFields(enabledFields);
    return this;
  }

  public InstitutionEntity toEntity() {
    return institution;
  }
}
