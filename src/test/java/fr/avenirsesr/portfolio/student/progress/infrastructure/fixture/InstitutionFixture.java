package fr.avenirsesr.portfolio.student.progress.infrastructure.fixture;

import fr.avenirsesr.portfolio.student.progress.domain.model.Institution;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import java.util.Set;
import java.util.UUID;

public class InstitutionFixture {

  private UUID id;
  private String name;
  private Set<EPortfolioType> enabledFields;
  private ELanguage language = ELanguage.FRENCH;

  private InstitutionFixture() {
    this.id = UUID.randomUUID();
    this.name = "Default Institution";
    this.enabledFields = Set.of(EPortfolioType.values());
  }

  public static InstitutionFixture create() {
    return new InstitutionFixture();
  }

  public InstitutionFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public InstitutionFixture withName(String name) {
    this.name = name;
    return this;
  }

  public InstitutionFixture withEnabledFields(Set<EPortfolioType> enabledFields) {
    this.enabledFields = enabledFields;
    return this;
  }

  public InstitutionFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public Institution toModel() {
    return Institution.toDomain(id, name, enabledFields);
  }
}
