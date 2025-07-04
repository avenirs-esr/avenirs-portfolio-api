package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.domain.port.output.repository.CohortRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.CohortMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake.FakeCohort;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
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

  private final CohortRepository cohortRepository;

  private Set<UserEntity> getRandomUsers(List<UserEntity> savedUsers) {
    int userCount =
        faker
            .call()
            .number()
            .numberBetween(SeederConfig.COHORT_NB_USERS_MIN, SeederConfig.COHORT_NB_USERS_MAX + 1);

    List<UserEntity> userList = new ArrayList<>(savedUsers);

    Collections.shuffle(userList);

    return new HashSet<>(userList.subList(0, userCount));
  }

  private TrainingPathEntity getRandomProgramProgress(
      List<TrainingPathEntity> savedTrainingPath) {
    int randomIndex = faker.call().number().numberBetween(0, savedProgramProgress.size());
    return savedTrainingPath.get(randomIndex);
  }

  public List<CohortEntity> seed(
      List<UserEntity> savedUsers, List<ProgramProgressEntity> savedProgramProgress) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    ValidationUtils.requireNonEmpty(savedProgramProgress, "program progress cannot be empty");

    log.info("Seeding cohorts...");

    List<CohortEntity> cohorts = new ArrayList<>();

    for (int i = 0; i < SeederConfig.COHORTS_NB; i++) {
      cohorts.add(
          FakeCohort.of(getRandomProgramProgress(savedProgramProgress), getRandomUsers(savedUsers))
              .toEntity());
    }

    cohortRepository.saveAll(cohorts.stream().map(CohortMapper::toDomain).toList());
    log.info("✔ {} cohorts created", cohorts.size());

    return cohorts;
  }
}
