package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

public class FakeSkillLevelProgress {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeSkillLevelProgress.class, SharedDataGenerator.class);

  private final SkillLevelProgressEntity skillLevelProgress;

  private FakeSkillLevelProgress(SkillLevelProgressEntity skillLevelProgress) {
    this.skillLevelProgress = skillLevelProgress;
  }

  public static FakeSkillLevelProgress create(StudentEntity student, SkillLevelEntity skillLevel) {
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    return new FakeSkillLevelProgress(
        SkillLevelProgressEntity.of(
            dataGenerator.with("id").uuid(),
            student,
            skillLevel,
            ESkillLevelStatus.NOT_STARTED,
            futureStartDate,
            futureEndDate,
            Instant.now(),
            Instant.now()));
  }

  public FakeSkillLevelProgress withStatus(ESkillLevelStatus status) {
    LocalDate pastStartDate = LocalDate.now().minus(Period.ofYears(2));
    LocalDate pastEndDate = LocalDate.now().minus(Period.ofYears(1));
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    skillLevelProgress.setStatus(status);
    switch (status) {
      case VALIDATED, FAILED -> {
        skillLevelProgress.setStartDate(pastStartDate);
        skillLevelProgress.setEndDate(pastEndDate);
      }
      case UNDER_ACQUISITION, UNDER_REVIEW -> {
        skillLevelProgress.setStartDate(pastStartDate);
        skillLevelProgress.setEndDate(futureEndDate);
      }
      case TO_BE_EVALUATED, NOT_STARTED -> {
        skillLevelProgress.setStartDate(futureStartDate);
        skillLevelProgress.setEndDate(futureEndDate);
      }
    }
    return this;
  }

  public SkillLevelProgressEntity toEntity() {
    return skillLevelProgress;
  }
}
