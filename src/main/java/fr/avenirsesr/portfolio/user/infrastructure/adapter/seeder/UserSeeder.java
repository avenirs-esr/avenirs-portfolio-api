package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class UserSeeder {
  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;

  public List<UserEntity> seed() {
    log.info("Seeding Users...");

    List<FakeUser> fakeUsers = new ArrayList<>();
    for (int i = 0; i < SeederConfig.USERS_NB_OF_STUDENT; i++) {
      fakeUsers.add(FakeUser.create().withEmail().withStudent());
    }
    for (int i = 0; i < SeederConfig.USERS_NB_OF_TEACHER; i++) {
      fakeUsers.add(FakeUser.create().withEmail().withTeacher());
    }
    for (int i = 0; i < SeederConfig.USERS_NB_OF_BOTH; i++) {
      fakeUsers.add(FakeUser.create().withEmail().withStudent().withTeacher());
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
    log.info("✔ {} externalUsers created", externalUsers.size());
    log.info("✔ {} students synced", SeederConfig.USERS_NB_OF_STUDENT);
    log.info("✔ {} teachers synced", SeederConfig.USERS_NB_OF_TEACHER);
    log.info("✔ {} students and teachers synced", SeederConfig.USERS_NB_OF_BOTH);
    log.info("✔ {} users created", users.size());

    return users;
  }
}
