package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.SeederConfig;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(TraceSeeder.class, SharedDataGenerator.class);

  private final TraceDatabaseRepository traceRepository;

  @Transactional
  public List<TraceEntity> seed(
      List<UserEntity> users, List<AdditionalSkillProgressEntity> additionalSkillsProgresses) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding Traces...");

    List<TraceEntity> traceList = new ArrayList<>();

    for (UserEntity user : users) {
      for (int i = 0;
          i
              < dataGenerator
                  .with("nb-traces")
                  .number(SeederConfig.TRACES_NB_MIN, SeederConfig.TRACES_NB_MAX);
          i++) {
        var fakeTrace = FakeTrace.of(user);

        if (dataGenerator.with("withAiUseJustification").bool())
          fakeTrace = fakeTrace.withAiUseJustification();
        if (dataGenerator.with("withPersonalNote").bool()) fakeTrace = fakeTrace.withPersonalNote();
        if (dataGenerator.with("isGroup").bool()) fakeTrace = fakeTrace.isGroup();

        fakeTrace =
            fakeTrace.withAdditionalSkillsProgress(
                additionalSkillsProgresses.subList(
                    0,
                    faker
                        .call("nb-additional-skills")
                        .random()
                        .nextInt(
                            SeederConfig.MIN_TRACES_ADDITIONAL_SKILL_PROGRESS,
                            SeederConfig.MAX_TRACES_ADDITIONAL_SKILL_PROGRESS)));

        var trace = fakeTrace.toEntity();
        traceList.add(trace);
      }
    }
    traceRepository.saveAllEntities(traceList);

    log.info("✔ {} traces created", traceList.size());

    return traceList;
  }
}
