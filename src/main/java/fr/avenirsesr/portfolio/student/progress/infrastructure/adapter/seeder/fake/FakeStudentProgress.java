package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

public class FakeStudentProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeStudentProgress.class, SharedDataGenerator.class);
  private final StudentProgressEntity studentProgress;
  private static final Period DEFAULT_STUDENT_PROGRESS_DURATION = Period.ofYears(1);

  private FakeStudentProgress(StudentProgressEntity studentProgress) {
    this.studentProgress = studentProgress;
  }

  public static FakeStudentProgress of(
      UserEntity student,
      TrainingPathEntity trainingPath,
      List<SkillLevelProgressEntity> skillLevels) {

    var today = LocalDate.now();

    var selectedYearTime = dataGenerator.with("selectedYearTime").pickIn(List.of(0, -1, 1));
    var startDate = LocalDate.of(today.getYear() + selectedYearTime, today.getMonth(), 1);

    return new FakeStudentProgress(
        StudentProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            trainingPath,
            startDate,
            startDate.plus(DEFAULT_STUDENT_PROGRESS_DURATION),
            skillLevels,
            today
                .minusDays(dataGenerator.with("minus-day").number(5, 365))
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            Instant.now()));
  }

  public FakeStudentProgress withStartDate(LocalDate startDate) {
    studentProgress.setStartDate(startDate);
    studentProgress.setEndDate(startDate.plus(DEFAULT_STUDENT_PROGRESS_DURATION));
    return this;
  }

  public StudentProgressEntity toEntity() {
    return studentProgress;
  }
}
