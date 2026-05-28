package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserPrincipalCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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

  private static final String USERS_PATH_FILE = "seeder/users.json";
  private static final String USER_PRINCIPALS_PATH_FILE = "seeder/user-principal.json";

  private final FileReader fileReader;
  private final UserService userService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<UserEntity> seed() {
    log.info("Seeding Users...");

    List<UserCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(USERS_PATH_FILE, new TypeReference<List<UserCreationData>>() {});
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

    Map<UUID, String> eppnByUserId = loadEppnByUserId();

    List<User> users = new ArrayList<>();

    creationData.forEach(
        userData -> {
          String eppn = eppnByUserId.get(userData.id());

          if (eppn == null || eppn.isBlank()) {
            throw new IllegalStateException(
                "Cannot create user: missing eppn for user id " + userData.id());
          }

          var user =
              userService.createUser(
                  userData.id(), userData.firstName(), userData.lastName(), userData.email(), eppn);

          users.add(user);
        });

    log.info("✔ {} users created", users.size());

    return users.stream().map(UserMapper.INSTANCE::fromDomain).toList();
  }

  private Map<UUID, String> loadEppnByUserId() {
    List<UserPrincipalCreationData> userPrincipals =
        fileReader.readJSON(
            USER_PRINCIPALS_PATH_FILE, new TypeReference<List<UserPrincipalCreationData>>() {});

    return userPrincipals.stream()
        .collect(
            Collectors.toMap(UserPrincipalCreationData::userId, UserPrincipalCreationData::eppn));
  }
}
