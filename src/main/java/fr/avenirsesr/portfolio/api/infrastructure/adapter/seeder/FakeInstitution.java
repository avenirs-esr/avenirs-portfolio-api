package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Institution;
import fr.avenirsesr.portfolio.api.domain.model.enums.ENavigationField;
import net.datafaker.Faker;

import java.util.Set;

public class FakeInstitution {
    private final static Faker faker = new Faker();
    private final Institution institution;

    private FakeInstitution(Institution institution) {
        this.institution = institution;
    }

    public static FakeInstitution create() {
        return new FakeInstitution(Institution.create(faker.university().name()));
    }

    public FakeInstitution withEnabledFiled(Set<ENavigationField> enabledFields) {
        institution.setEnabledFields(enabledFields);
        return this;
    }

    public Institution toModel() {
        return institution;
    }
}
