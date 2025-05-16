package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import net.datafaker.Faker;

import java.util.Set;

public class FakeSkill {
    private final static Faker faker = new Faker();
    private final Skill skill;

    private FakeSkill(Skill skill) {
        this.skill = skill;
    }

    public static FakeSkill of(Set<SkillLevel> skillLevels) {
        return new FakeSkill(Skill.create("Skill %s".formatted(faker.lorem().word()), skillLevels));
    }

    public Skill toModel() {
        return skill;
    }
}
