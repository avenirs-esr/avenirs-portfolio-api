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
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceSeeder {
  private static final Faker faker = new FakerProvider().call();

  private final TraceRepository traceRepository;

  private UserEntity getRandomUserOf(List<UserEntity> users) {
    int randomIndex = faker.number().numberBetween(0, users.size());
    return users.get(randomIndex);
  }

  public List<TraceEntity> seed(List<UserEntity> users) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding Traces...");

    List<TraceEntity> traceList = new ArrayList<>();

    for (int i = 0; i < SeederConfig.TRACES_NB; i++) {
      var fakeTrace = FakeTrace.of(getRandomUserOf(users));

      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.withAiUseJustification();
      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.withPersonalNote();
      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.isGroup();

      var trace = fakeTrace.toEntity();
      traceList.add(trace);
    }

    traceRepository.saveAll(traceList.stream().map(TraceMapper::toDomain).toList());

    log.info("✔ {} traces created", traceList.size());

    return traceList;
  }
}
