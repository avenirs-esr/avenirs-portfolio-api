package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserSeederTest extends ContainerConfigurationTest {

  @Autowired private UserSeeder userSeeder;

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
  }
}
