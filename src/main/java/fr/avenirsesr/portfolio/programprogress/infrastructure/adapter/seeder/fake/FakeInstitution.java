package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.domain.model.Institution;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.translation.InstitutionTranslation;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FakeInstitution {
  private static final FakerProvider faker = new FakerProvider();
  private final Institution institution;
  private final List<InstitutionTranslation> translations;

  private FakeInstitution(Institution institution) {
    this.institution = institution;
    this.translations = new ArrayList<>();
  }

  public static FakeInstitution create() {
    var fakeInstitution =
        new FakeInstitution(
            Institution.create(
                UUID.fromString(faker.call().internet().uuid()), faker.call().university().name()));
    fakeInstitution.addTranslation(ELanguage.FRENCH);
    return fakeInstitution;
  }

  public FakeInstitution addTranslation(ELanguage language) {
    translations.add(
        new InstitutionTranslation(
            language, String.format("%s %s", institution.getName(), language.getCode())));

    return this;
  }

  public FakeInstitution withEnabledFiled(Set<EPortfolioType> enabledFields) {
    institution.setEnabledFields(enabledFields);
    return this;
  }

  public Institution toModel() {
    return institution;
  }

  public List<InstitutionTranslation> getTranslations() {
    return translations;
  }
}
