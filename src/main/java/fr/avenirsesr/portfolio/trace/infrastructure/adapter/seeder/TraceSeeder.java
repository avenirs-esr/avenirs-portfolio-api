package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class TraceSeeder {

  private static final FakerProvider faker = new FakerProvider();
  private static final int NB_TRACES = 20;

  private final TraceRepository traceRepository;

  private UserEntity getRandomUserOf(List<UserEntity> users) {
    int randomIndex = faker.call().number().numberBetween(0, users.size());
    return users.get(randomIndex);
  }

  public List<TraceEntity> seed(List<UserEntity> users) {
    if (users == null || users.isEmpty()) {
      throw new IllegalArgumentException("The list of users is empty");
    }

    log.info("Seeding Traces...");

    List<TraceEntity> traceList = new ArrayList<>();

    for (int i = 0; i < NB_TRACES; i++) {
      traceList.add(FakeTrace.of(getRandomUserOf(users)).toEntity());
    }

    traceRepository.saveAll(traceList.stream().map(TraceMapper::toDomain).toList());

    log.info("✓ {} traces created", traceList.size());

    return traceList;
  }
}
