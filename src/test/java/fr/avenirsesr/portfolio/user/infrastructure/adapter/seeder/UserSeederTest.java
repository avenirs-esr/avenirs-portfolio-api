package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class UserSeederTest {

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
