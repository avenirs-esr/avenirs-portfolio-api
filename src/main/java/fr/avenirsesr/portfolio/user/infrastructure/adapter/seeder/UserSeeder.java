package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeExternalUser;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(UserSeeder.class, SharedDataGenerator.class);

  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;

  @Transactional
  public List<UserEntity> seed() {
    log.info("Seeding Users...");

    List<FakeUser> fakeUsers = new ArrayList<>();
    for (int i = 0; i < SeederConfig.USERS_NB; i++) {
      fakeUsers.add(FakeUser.create());
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
                            dataGenerator
                                .with("external-user-category")
                                .pickIn(EUserCategory.class))
                        .toEntity())
            .toList();

    externalUserRepository.saveAll(
        externalUsers.stream().map(ExternalUserMapper::toDomain).toList());

    log.info("✔ {} externalUsers created", externalUsers.size());
    log.info("✔ {} users created", users.size());

    return users;
  }
}
