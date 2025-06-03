package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import java.util.Set;
import java.util.UUID;

public class FakeInstitution {
  private static final FakerProvider faker = new FakerProvider();
  private final Institution institution;

  private FakeInstitution(Institution institution) {
    this.institution = institution;
  }

  public static FakeInstitution create() {
    return new FakeInstitution(
        Institution.create(
            UUID.fromString(faker.call().internet().uuid()), faker.call().university().name()));
  }

  public FakeInstitution withEnabledFiled(Set<EPortfolioType> enabledFields) {
    institution.setEnabledFields(enabledFields);
    return this;
  }

  public Institution toModel() {
    return institution;
  }
}
