package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.AmsDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.FakePeriod;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FakeAMS {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeAMS.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<AmsDataGenerator> amsDataGenerator =
      new DataGeneratorProvider<AmsDataGenerator>().init(FakeAMS.class, AmsDataGenerator.class);
  private final AMSEntity ams;

  private FakeAMS(AMSEntity ams) {
    this.ams = ams;
  }

  public static FakeAMS of(UserEntity user) {
    FakePeriod<Instant> period = FakePeriod.createMin24hoursInstantPeriodInAcademicPeriod();
    var entity =
        AMSEntity.of(
            dataGenerator.with("id").uuid(),
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
                dataGenerator.with("id").uuid(),
                ELanguage.FRENCH,
                amsDataGenerator.with("title", ELanguage.FRENCH).title(),
                entity)));

    return new FakeAMS(entity);
  }

  public FakeAMS addTranslation(ELanguage language) {
    var translations = new java.util.HashSet<>(Set.copyOf(ams.getTranslations()));
    translations.add(
        AMSTranslationEntity.of(
            dataGenerator.with("id").uuid(),
            language,
            amsDataGenerator.with("title-translation", language).title(),
            ams));

    ams.setTranslations(translations);

    return this;
  }

  public FakeAMS withSkillLevel(List<SkillLevelProgressEntity> skillLevels) {
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
