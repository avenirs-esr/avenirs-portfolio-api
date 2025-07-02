package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class FakeSkillLevel {
  private static final FakerProvider faker = new FakerProvider();
  private final SkillLevel skillLevel;

  private FakeSkillLevel(SkillLevel skillLevel) {
    this.skillLevel = skillLevel;
  }

  public static FakeSkillLevel create() {
    return new FakeSkillLevel(
            SkillLevel.create(
                UUID.fromString(faker.call().internet().uuid()),
                "Niv. %s".formatted(faker.call().lorem().character()),
                faker.call().lorem().sentence()))
        .withStatus(ESkillLevelStatus.NOT_STARTED);
  }

  public static FakeSkillLevel of(SkillLevel skillLevel, ELanguage language) {
    return new FakeSkillLevel(
        SkillLevel.create(
            skillLevel.getId(),
            String.format("%s %s", skillLevel.getName(), language.getCode()),
            String.format("%s %s", skillLevel.getDescription(), language.getCode())));
  }

  public FakeSkillLevel withStatus(ESkillLevelStatus status) {
    LocalDate pastStartDate = LocalDate.now().minus(Period.ofYears(2));
    LocalDate pastEndDate = LocalDate.now().minus(Period.ofYears(1));
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
    skillLevel.setStatus(status);
    switch (status) {
      case VALIDATED, FAILED -> {
        skillLevel.setStartDate(pastStartDate);
        skillLevel.setEndDate(pastEndDate);
      }
      case UNDER_ACQUISITION, UNDER_REVIEW -> {
        skillLevel.setStartDate(pastStartDate);
        skillLevel.setEndDate(futureEndDate);
      }
      case TO_BE_EVALUATED, NOT_STARTED -> {
        skillLevel.setStartDate(futureStartDate);
        skillLevel.setEndDate(futureEndDate);
      }
    }
    return this;
  }

  public SkillLevel toModel() {
    return skillLevel;
  }
}
