package fr.avenirsesr.portfolio.program.infrastructure.fixture;

import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import java.time.Instant;
import java.util.*;

public class TrainingPathFixture {

  private UUID id;
  private Program program;
  private Set<SkillLevel> skillLevels;
  private Instant createdAt;
  private Instant updatedAt;

  private TrainingPathFixture() {
    this.id = UUID.randomUUID();
    this.program = ProgramFixture.create().toModel();
    this.skillLevels = new LinkedHashSet<>();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static TrainingPathFixture create() {
    return new TrainingPathFixture();
  }

  public static TrainingPathFixture createWithAPC() {
    return new TrainingPathFixture().withProgram(ProgramFixture.createWithAPC().toModel());
  }

  public static TrainingPathFixture createWithoutAPC() {
    return new TrainingPathFixture().withProgram(ProgramFixture.createWithoutAPC().toModel());
  }

  public TrainingPathFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public TrainingPathFixture withProgram(Program program) {
    this.program = program;
    return this;
  }

  public TrainingPathFixture withSkillLevels(Set<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public TrainingPathFixture withSkillLevels(int count) {
    this.skillLevels = new HashSet<SkillLevel>(SkillLevelFixture.create().withCount(count));
    return this;
  }

  public TrainingPathFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TrainingPathFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public TrainingPath toModel() {
    return TrainingPath.toDomain(id, program, skillLevels, createdAt, updatedAt);
  }
}
