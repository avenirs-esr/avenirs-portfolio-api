package fr.avenirsesr.portfolio.student.skill.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.SharedFakerDataGenerator;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.data.SkillProgressFakerDataGenerator;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class SkillProgressFixture {
  private static final SharedFakerDataGenerator dataGenerator =
      generator(new SharedFakerDataGenerator(), "shared");
  private static final SkillProgressFakerDataGenerator skillProgressGenerator =
      generator(new SkillProgressFakerDataGenerator(), "skill-progress");

  private static <T extends DataGenerator> T generator(T generator, String seed) {
    generator.setRandom(new Random(seed.hashCode()));
    generator.setLanguage(ELanguage.FALLBACK);
    return generator;
  }

  private UUID id;
  private Student student;
  private Skill skill;
  private ESkillLevel level;
  private String reflection;
  private boolean valorized;
  private Instant createdAt;
  private Instant updatedAt;

  private SkillProgressFixture() {
    this.id = dataGenerator.uuid();
    this.student = StudentFixture.create().toModel();
    this.skill = SkillFixture.create().toModel();
    this.level = dataGenerator.pickIn(ESkillLevel.class);
    this.reflection = skillProgressGenerator.reflection();
    this.valorized = false;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static SkillProgressFixture create() {
    return new SkillProgressFixture();
  }

  public SkillProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillProgressFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public SkillProgressFixture withSkill(Skill skill) {
    this.skill = skill;
    return this;
  }

  public SkillProgressFixture withLevel(ESkillLevel level) {
    this.level = level;
    return this;
  }

  public SkillProgressFixture withReflection(String reflection) {
    this.reflection = reflection;
    return this;
  }

  public SkillProgressFixture withValorized(boolean valorized) {
    this.valorized = valorized;
    return this;
  }

  public SkillProgressFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillProgressFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SkillProgress toModel() {
    return SkillProgress.toDomain(
        id, student, skill, level, reflection, valorized, createdAt, updatedAt);
  }
}
