package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
import java.util.*;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {
  private static final String PATH_FILE = "seeder/users.json";
  private final FileReader fileReader;
  private final UserService userService;
  private final ClockService clockService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<UserEntity> seed() {
    log.info("Seeding Users...");

    List<UserCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<UserCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.USERS_NB)
                  .mapToObj(i -> FakeUser.create().toEntity())
                  .map(
                      user ->
                          new UserCreationData(
                              user.getId(),
                              user.getFirstName(),
                              user.getLastName(),
                              user.getEmail()))
                  .toList();
        };

    List<User> users = new ArrayList<>();

    creationData.forEach(
        userData -> {
          var user =
              userService.createUser(
                  userData.id(), userData.firstName(), userData.lastName(), userData.email());
          users.add(user);
        });

    log.info("✔ {} users created", users.size());
    return users.stream().map(UserMapper.INSTANCE::fromDomain).toList();
  }
}
