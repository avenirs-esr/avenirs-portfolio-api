package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.CohortRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake.FakeCohort;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class CohortSeeder {

  private static final FakerProvider faker = new FakerProvider();

  private static final int DEFAULT_NB_COHORTS = 50;
  private static final int NB_USERS_MIN_PER_COHORT = 1;
  private static final int NB_USERS_MAX_PER_COHORT = 50;

  private int nbCohorts = DEFAULT_NB_COHORTS;

  private final CohortRepository cohortRepository;
  List<User> users = List.of();

  List<ProgramProgress> programProgressSet = List.of();

  public CohortSeeder withUsers(List<User> users) {
    this.users = users;
    return this;
  }

  public CohortSeeder withProgramProgressSet(List<ProgramProgress> programProgressSet) {
    this.programProgressSet = programProgressSet;
    return this;
  }

  private void checkIfInitialized() throws IllegalStateException {
    if (users.isEmpty() || programProgressSet.isEmpty()) {
      log.error(
          "CohortSeeder is not initialized: withUsers and withProgramProgressSet must be called"
              + " before seeding");
      throw new IllegalStateException("CohortSeeder is not initialized");
    }
    if (users.size() < NB_USERS_MAX_PER_COHORT) {
      log.error("Not enough users to seed cohorts");
      throw new IllegalStateException("Not enough users to seed cohorts");
    }
  }

  private Set<User> getRandomUsers() {
    int userCount =
        faker.call().number().numberBetween(NB_USERS_MIN_PER_COHORT, NB_USERS_MAX_PER_COHORT + 1);

    List<User> userList = new ArrayList<>(users);

    Collections.shuffle(userList);

    return new HashSet<>(userList.subList(0, userCount));
  }

  private ProgramProgress getRandomProgramProgress() {
    int randomIndex = faker.call().number().numberBetween(0, programProgressSet.size());
    return programProgressSet.get(randomIndex);
  }

  public List<Cohort> seed() {
    log.info("Seeding cohorts...");
    checkIfInitialized();

    List<Cohort> cohorts = new ArrayList<>();

    for (int i = 0; i < nbCohorts; i++) {
      Set<User> cohortUsers = getRandomUsers();
      ProgramProgress cohortProgramProgresses = getRandomProgramProgress();

      cohorts.add(FakeCohort.of(cohortProgramProgresses, cohortUsers).toModel());
    }

    cohortRepository.saveAll(cohorts);
    log.info("✓ {} cohorts created", cohorts.size());
    return cohorts;
  }
}
