package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.Set;
import java.util.UUID;

public class FakeInstitution {
  private static final FakerProvider faker = new FakerProvider();
  private final InstitutionEntity institution;

  private FakeInstitution(InstitutionEntity institution) {
    this.institution = institution;
  }

  public static FakeInstitution create() {
    var entity = InstitutionEntity.of(UUID.fromString(faker.call().internet().uuid()), Set.of());
    var fakeInstitution = new FakeInstitution(entity);

    entity.setTranslations(
        Set.of(
            InstitutionTranslationEntity.of(
                UUID.fromString(faker.call().internet().uuid()),
                ELanguage.FRENCH,
                faker.call().university().name(),
                entity)));

    return fakeInstitution;
  }

  public FakeInstitution addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(institution.getTranslations()));

    translations.add(
        InstitutionTranslationEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            language,
            faker.call().university().name(),
            institution));

    institution.setTranslations(translations);

    return this;
  }

  public FakeInstitution withEnabledFiled(Set<EPortfolioType> enabledFields) {
    institution.setEnabledFields(enabledFields);
    return this;
  }

  public InstitutionEntity toEntity() {
    return institution;
  }
}
