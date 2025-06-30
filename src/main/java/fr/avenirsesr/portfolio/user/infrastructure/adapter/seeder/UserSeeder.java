package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeExternalUser;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
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
  private static final int DEF_NB_USERS = 100;
  private static final double PROBABILITY_OF_STUDENT = 0.8;
  private static final double PROBABILITY_OF_BOTH = 0.08;

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

  public List<UserEntity> seed() {
    log.info("Seeding Users...");

    List<FakeUser> fakeUsers = new ArrayList<>();
    for (int i = 0; i < DEF_NB_USERS; i++) {
      fakeUsers.add(createFakeUser());
    }

    List<UserEntity> users =
        fakeUsers.stream().map(FakeUser::toEntity).collect(Collectors.toList());
    userRepository.saveAll(users.stream().map(UserMapper::toDomain).toList());

    var externalUsers =
        users.stream()
            .map(
                user ->
                    FakeExternalUser.of(
                            user,
                            user.getStudent().isPresent() && user.getStudent().get().isActive()
                                ? EUserCategory.STUDENT
                                : EUserCategory.TEACHER)
                        .toEntity())
            .toList();

    externalUserRepository.saveAll(
        externalUsers.stream().map(ExternalUserMapper::toDomain).toList());

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
