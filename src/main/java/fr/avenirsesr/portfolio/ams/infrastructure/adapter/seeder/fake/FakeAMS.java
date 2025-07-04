package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakePeriod;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class FakeAMS {
  private static final FakerProvider faker = new FakerProvider();
  private final AMSEntity ams;

  private FakeAMS(AMSEntity ams) {
    this.ams = ams;
  }

  public static FakeAMS of(UserEntity user) {
    FakePeriod<Instant> period = FakePeriod.createMin24hoursInstantPeriodInAcademicPeriod();
    var entity =
        AMSEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            user,
            EAmsStatus.NOT_STARTED,
            period.getStartDate(),
            period.getEndDate(),
            Set.of(),
            Set.of(),
            Set.of());

    entity.setTranslations(
        Set.of(
            AMSTranslationEntity.of(
                UUID.fromString(faker.call().internet().uuid()),
                ELanguage.FRENCH,
                faker.call().name().title(),
                entity)));

    return new FakeAMS(entity);
  }

  public FakeAMS addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(ams.getTranslations()));
    translations.add(
        AMSTranslationEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            language,
            "%s - [%s]".formatted(faker.call().name().title(), language.getCode()),
            ams));

    ams.setTranslations(translations);

    return this;
  }

  public FakeAMS withSkillLevel(List<SkillLevelEntity> skillLevels) {
    ams.setSkillLevels(skillLevels);
    skillLevels.forEach(
        skillLevel ->
            skillLevel.setAmses(
                Stream.concat(skillLevel.getAmses().stream(), Stream.of(ams)).toList()));
    return this;
  }

  public FakeAMS withCohorts(Set<CohortEntity> cohorts) {
    ams.setCohorts(cohorts);
    return this;
  }

  public FakeAMS withTraces(List<TraceEntity> traces) {
    ams.setTraces(traces);
    return this;
  }

  public FakeAMS withStatus(EAmsStatus status) {
    ams.setStatus(status);
    return this;
  }

  public AMSEntity toEntity() {
    return ams;
  }
}
