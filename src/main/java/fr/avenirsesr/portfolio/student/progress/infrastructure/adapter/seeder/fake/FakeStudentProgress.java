package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public class FakeStudentProgress {
  private static final FakerProvider faker = new FakerProvider();
  private final StudentProgressEntity studentProgress;

  private FakeStudentProgress(StudentProgressEntity studentProgress) {
    this.studentProgress = studentProgress;
  }

  public static FakeStudentProgress of(
      UserEntity student, TrainingPathEntity trainingPath, SkillLevelEntity skillLevel) {
    LocalDate today = LocalDate.now();
    return new FakeStudentProgress(
        StudentProgressEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            student,
            trainingPath,
            skillLevel,
            faker.call().options().option(ESkillLevelStatus.class),
            today
                .minusDays(faker.call().number().numberBetween(5, 365))
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            Instant.now()));
  }

  public StudentProgressEntity toEntity() {
    return studentProgress;
  }
}
