package fr.avenirsesr.portfolio.declaredskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.seeder.DeclaredSkillProgressDataGenerator;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DeclaredSkillProgressFixture {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(DeclaredSkillProgressFixture.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<DeclaredSkillProgressDataGenerator>
      declaredSkillProgressGenerator =
          new DataGeneratorProvider<DeclaredSkillProgressDataGenerator>()
              .init(DeclaredSkillProgressFixture.class, DeclaredSkillProgressDataGenerator.class);

  private UUID id;
  private Student student;
  private DeclaredSkill skill;
  private EDeclaredSkillLevel level;
  private String reflection;
  private Instant createdAt;
  private Instant updatedAt;

  private DeclaredSkillProgressFixture() {
    this.id = dataGenerator.with("id").uuid();
    this.student = StudentFixture.create().toModel();
    this.skill =
        DeclaredSkill.create(
            UUID.randomUUID(),
            "Test Skill",
            EExternalSkillType.ROME4,
            List.of("Category", "Subcategory"));
    this.level = dataGenerator.with("level").pickIn(EDeclaredSkillLevel.class);
    this.reflection = declaredSkillProgressGenerator.with("sentence").reflection();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static DeclaredSkillProgressFixture create() {
    return new DeclaredSkillProgressFixture();
  }

  public DeclaredSkillProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public DeclaredSkillProgressFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public DeclaredSkillProgressFixture withSkill(DeclaredSkill skill) {
    this.skill = skill;
    return this;
  }

  public DeclaredSkillProgressFixture withLevel(EDeclaredSkillLevel level) {
    this.level = level;
    return this;
  }

  public DeclaredSkillProgressFixture withReflection(String reflection) {
    this.reflection = reflection;
    return this;
  }

  public DeclaredSkillProgressFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public DeclaredSkillProgressFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public DeclaredSkillProgress toModel() {
    return DeclaredSkillProgress.toDomain(
        id, student, skill, level, reflection, createdAt, updatedAt);
  }
}
