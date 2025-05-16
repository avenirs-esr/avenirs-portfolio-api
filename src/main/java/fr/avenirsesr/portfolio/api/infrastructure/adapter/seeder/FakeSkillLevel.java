package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import net.datafaker.Faker;

public class FakeSkillLevel {
    private final static Faker faker = new Faker();
    private final SkillLevel skillLevel;

    private FakeSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public static FakeSkillLevel create() {
        return new FakeSkillLevel(SkillLevel.create("Niv. %s".formatted(faker.lorem().character())));
    }

    public FakeSkillLevel withStatus(ESkillLevelStatus status) {
        skillLevel.setStatus(status);
        return this;
    }

    public SkillLevel toModel() {
        return skillLevel;
    }

}
