package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StudentProgressFixture {
  private UUID id;
  private Student student;
  private TrainingPath trainingPath;
  private LocalDate startDate;
  private LocalDate endDate;
  private List<SkillLevelProgress> skillLevels;
  private Instant createdAt;
  private Instant updatedAt;

  private static final Period DEFAULT_STUDENT_PROGRESS_DURATION = Period.ofYears(1);

  private StudentProgressFixture(
      UUID id,
      Student student,
      TrainingPath trainingPath,
      LocalDate startDate,
      LocalDate endDate,
      List<SkillLevelProgress> skillLevels) {
    this.id = id;
    this.student = student;
    this.trainingPath = trainingPath;
    this.startDate = startDate;
    this.endDate = endDate;
    this.skillLevels = skillLevels;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static StudentProgressFixture create() {
    var trainingPathSkillLevels =
        Set.of(
            SkillLevelFixture.create().toModel(),
            SkillLevelFixture.create().toModel(),
            SkillLevelFixture.create().toModel(),
            SkillLevelFixture.create().toModel());
    var trainingPath =
        TrainingPathFixture.create().withSkillLevels(trainingPathSkillLevels).toModel();
    var student = StudentFixture.create().toModel();
    var skillLevelsProgress =
        trainingPathSkillLevels.stream()
            .map(skillLevel -> SkillLevelProgressFixture.create(student, skillLevel).toModel())
            .toList();
    var startDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
    return new StudentProgressFixture(
        UUID.randomUUID(),
        student,
        trainingPath,
        startDate,
        startDate.plus(DEFAULT_STUDENT_PROGRESS_DURATION),
        skillLevelsProgress);
  }

  public StudentProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public StudentProgressFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public StudentProgressFixture withTrainingPath(TrainingPath trainingPath) {
    this.trainingPath = trainingPath;
    return this;
  }

  public StudentProgressFixture withSkillLevels(List<SkillLevelProgress> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public StudentProgressFixture withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    this.endDate = startDate.plus(DEFAULT_STUDENT_PROGRESS_DURATION);
    return this;
  }

  public StudentProgressFixture withStartDate(LocalDate startDate, Period period) {
    this.startDate = startDate;
    this.endDate = startDate.plus(period);
    return this;
  }

  public StudentProgressFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public StudentProgressFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public StudentProgress toModel() {
    return StudentProgress.toDomain(
        id, student, trainingPath, startDate, endDate, skillLevels, createdAt, updatedAt);
  }
}
