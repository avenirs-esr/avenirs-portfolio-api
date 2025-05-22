package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELearningMethod;
import java.util.Set;
import net.datafaker.Faker;

public class FakeInstitution {
  private static final Faker faker = new Faker();
  private final Institution institution;

  private FakeInstitution(Institution institution) {
    this.institution = institution;
  }

  public static FakeInstitution create() {
    return new FakeInstitution(Institution.create(faker.university().name()));
  }

  public FakeInstitution withEnabledFiled(Set<ELearningMethod> enabledFields) {
    institution.setEnabledFields(enabledFields);
    return this;
  }

  public Institution toModel() {
    return institution;
  }
}
