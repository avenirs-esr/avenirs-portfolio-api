package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeSkillLevelProgress {
  private static final Faker faker = new FakerProvider().call();
  private final SkillLevelProgressEntity skillLevelProgress;

  private FakeSkillLevelProgress(SkillLevelProgressEntity skillLevelProgress) {
    this.skillLevelProgress = skillLevelProgress;
  }

  public static FakeSkillLevelProgress create(UserEntity student, SkillLevelEntity skillLevel) {
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    return new FakeSkillLevelProgress(
        SkillLevelProgressEntity.of(
            UUID.fromString(faker.internet().uuid()),
            student,
            skillLevel,
            ESkillLevelStatus.NOT_STARTED,
            futureStartDate,
            futureEndDate,
            List.of(),
            List.of()));
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

  public FakeSkillLevelProgress withTraces(List<TraceEntity> traces) {
    this.skillLevelProgress.setTraces(traces);
    return this;
  }

  public FakeSkillLevelProgress withAmses(List<AMSEntity> amses) {
    this.skillLevelProgress.setAmses(amses);
    return this;
  }

  public SkillLevelProgressEntity toEntity() {
    return skillLevelProgress;
  }
}
