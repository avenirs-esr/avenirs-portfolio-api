package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.seeder.AdditionalSkillProgressDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AdditionalSkillProgressFixture {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(AdditionalSkillProgressFixture.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<AdditionalSkillProgressDataGenerator>
      additionalSkillProgressGenerator =
          new DataGeneratorProvider<AdditionalSkillProgressDataGenerator>()
              .init(
                  AdditionalSkillProgressFixture.class, AdditionalSkillProgressDataGenerator.class);

  private UUID id;
  private Student student;
  private AdditionalSkill skill;
  private EAdditionalSkillLevel level;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;

  private AdditionalSkillProgressFixture() {
    this.id = dataGenerator.with("id").uuid();
    this.student = StudentFixture.create().toModel();
    this.skill =
        AdditionalSkill.create(
            UUID.randomUUID(),
            "Test Skill",
            EAdditionalSkillType.ROME4,
            List.of("Category", "Subcategory"));
    this.level = dataGenerator.with("level").pickIn(EAdditionalSkillLevel.class);
    this.description = additionalSkillProgressGenerator.with("sentence").description();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
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

  public AdditionalSkillProgressFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public AdditionalSkillProgressFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public AdditionalSkillProgressFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public AdditionalSkillProgress toModel() {
    return AdditionalSkillProgress.toDomain(
        id, student, skill, level, description, createdAt, updatedAt);
  }
}
