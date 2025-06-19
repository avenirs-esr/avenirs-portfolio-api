package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeExternalUser;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeUser;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class UserSeeder {

  private static final Faker faker = new FakerProvider().call();
  private static final int DEF_NB_USERS = 20;
  private static final double PROBABILITY_OF_STUDENT = 0.8;
  private static final double PROBABILITY_OF_BOTH = 0.08;

  private int nbUsers = DEF_NB_USERS;
  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;
  private int nbStudents = 0;
  private int nbTeachers = 0;
  private int nbBoth = 0;

  private FakeUser createFakeUser() {

    if (faker.random().nextDouble() < PROBABILITY_OF_STUDENT) {
      nbStudents++;
      if (faker.random().nextDouble() < PROBABILITY_OF_BOTH) {
        nbBoth++;
        FakeUser.create().withEmail().withStudent().withTeacher();
      }
      return FakeUser.create().withEmail().withStudent();
    }
    nbTeachers++;
    return FakeUser.create().withEmail().withTeacher();
  }

  public List<User> seed() {
    log.info("Seeding Users...");

    List<FakeUser> fakeUsers = new ArrayList<>();
    for (int i = 0; i < nbUsers; i++) {
      fakeUsers.add(createFakeUser());
    }

    List<User> users = fakeUsers.stream().map(FakeUser::toModel).collect(Collectors.toList());
    userRepository.saveAll(users);

    var externalUsers =
        users.stream()
            .map(
                user ->
                    FakeExternalUser.of(
                            user, user.isStudent() ? EUserCategory.STUDENT : EUserCategory.TEACHER)
                        .toModel())
            .toList();

    externalUserRepository.saveAll(externalUsers);

    var students = fakeUsers.stream().map(FakeUser::getStudent).filter(Objects::nonNull).toList();
    userRepository.saveAllStudents(students);

    var teachers = fakeUsers.stream().map(FakeUser::getTeacher).filter(Objects::nonNull).toList();
    userRepository.saveAllTeachers(teachers);
    log.info("✓ {} externalUsers created", externalUsers.size());
    log.info("✓ {} students synced", nbStudents);
    log.info("✓ {} teachers synced", nbTeachers);
    log.info("✓ {} students and teachers synced", nbBoth);
    log.info("✓ {} users created", users.size());
    return users;
  }
}
