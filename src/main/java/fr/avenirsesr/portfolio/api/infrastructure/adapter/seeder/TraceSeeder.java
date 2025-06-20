package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakerProvider;
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
  private static final int DEF_NB_TRACES = 20;

  private final TraceRepository traceRepository;
  private List<User> users = List.of();
  private int nbTraces = DEF_NB_TRACES;

  public TraceSeeder withUsers(List<User> users) {
    this.users = users;
    return this;
  }

  private void checkIfInitialized() throws IllegalStateException {
    if (users.isEmpty()) {
      log.error("TraceSeeder is not initialized: withUsers must be called before seeding");
      throw new IllegalStateException("TraceSeeder is not initialized");
    }
  }

  private User getRandomUser() {
    int randomIndex = faker.call().number().numberBetween(0, users.size());
    return users.get(randomIndex);
  }

  public List<Trace> seed() {
    log.info("Seeding Traces...");

    checkIfInitialized();

    List<Trace> traceList = new ArrayList<>();

    for (int i = 0; i < nbTraces; i++) {
      traceList.add(FakeTrace.of(getRandomUser()).toModel());
    }

    traceRepository.saveAll(traceList);

    log.info("✓ {} traces created", traceList.size());
    return traceList;
  }
}
