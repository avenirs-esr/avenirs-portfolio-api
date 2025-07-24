package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.UUID;

public class AdditionalSkillProgressFixture {
  private static final FakerProvider faker = new FakerProvider();

  private UUID id;
  private Student student;
  private AdditionalSkill skill;
  private EAdditionalSkillLevel level;

  private AdditionalSkillProgressFixture() {
    this.id = UUID.fromString(faker.call().internet().uuid());
    this.student = UserFixture.create().toModel().toStudent();
    this.skill = AdditionalSkillFixture.create().toModel();
    this.level = faker.call().options().option(EAdditionalSkillLevel.class);
  }

  public static AdditionalSkillProgressFixture create() {
    return new AdditionalSkillProgressFixture();
  }

  public AdditionalSkillProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public AdditionalSkillProgressFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public AdditionalSkillProgressFixture withSkill(AdditionalSkill skill) {
    this.skill = skill;
    return this;
  }

  public AdditionalSkillProgressFixture withLevel(EAdditionalSkillLevel level) {
    this.level = level;
    return this;
  }

  public AdditionalSkillProgress toModel() {
    return AdditionalSkillProgress.toDomain(id, student, skill, level);
  }
}
