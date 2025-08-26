package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceSeeder {
  private static final FakerProvider faker = new FakerProvider().init(TraceSeeder.class);

  private final TraceRepository traceRepository;

  public List<TraceEntity> seed(List<UserEntity> users) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding Traces...");

    List<TraceEntity> traceList = new ArrayList<>();

    for (UserEntity user : users) {
      for (int i = 0;
          i
              < faker
                  .call("nb-traces")
                  .random()
                  .nextInt(SeederConfig.TRACES_NB_MIN, SeederConfig.TRACES_NB_MAX);
          i++) {
        var fakeTrace = FakeTrace.of(user);

        if (faker.call("withAiUseJustification").random().nextBoolean())
          fakeTrace = fakeTrace.withAiUseJustification();
        if (faker.call("withPersonalNote").random().nextBoolean())
          fakeTrace = fakeTrace.withPersonalNote();
        if (faker.call("isGroup").random().nextBoolean()) fakeTrace = fakeTrace.isGroup();

        var trace = fakeTrace.toEntity();
        traceList.add(trace);
      }
    }
    traceRepository.saveAll(traceList.stream().map(TraceMapper::toDomain).toList());

    log.info("✔ {} traces created", traceList.size());

    return traceList;
  }
}
