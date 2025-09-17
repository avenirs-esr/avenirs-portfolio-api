package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class InstitutionFixture {

  private UUID id;
  private String name;
  private Set<EPortfolioType> enabledFields;
  private ELanguage language = ELanguage.FRENCH;
  private Instant createdAt;
  private Instant updatedAt;

  private InstitutionFixture() {
    this.id = UUID.randomUUID();
    this.name = "Default Institution";
    this.enabledFields = Set.of(EPortfolioType.values());
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
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

  public InstitutionFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public InstitutionFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Institution toModel() {
    return Institution.toDomain(id, name, enabledFields, createdAt, updatedAt);
  }
}
