package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserSeederTest extends ContainerConfigurationTest {

  @Autowired private UserSeeder userSeeder;

  @Autowired private UserRepository userRepository;
  @Autowired private ExternalUserRepository externalUserRepository;

  private static List<UserEntity> users;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
  }

  @Test
  void seed_shouldReturnNonEmptyUsersList() {
    BddLogger.given("a user seeder");
    BddLogger.when("seeding users");
    BddLogger.then("it should return users");
    assertNotNull(users);
    assertFalse(users.isEmpty());

    int expectedTotal = SeederConfig.USERS_NB;
    assertEquals(expectedTotal, users.size());
  }

  @Test
  void seed_shouldCallRepositories() {
    BddLogger.given("a user seeder");
    // Mock les repositories pour vérifier les appels
    UserRepository mockUserRepo = mock(UserRepository.class);
    ExternalUserRepository mockExternalRepo = mock(ExternalUserRepository.class);
    UserSeeder seederWithMock = new UserSeeder(mockUserRepo, mockExternalRepo);

    BddLogger.when("seeding users");
    List<UserEntity> result = seederWithMock.seed();

    BddLogger.then("it should call repositories");
    verify(mockUserRepo, atLeastOnce()).saveAll(any());
    verify(mockExternalRepo, atLeastOnce()).saveAll(any());
  }
}
