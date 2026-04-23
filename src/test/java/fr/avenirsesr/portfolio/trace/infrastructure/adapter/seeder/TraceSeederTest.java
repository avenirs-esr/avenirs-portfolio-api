package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class TraceSeederTest extends ContainerConfigurationTest {

  @Autowired private TraceSeeder traceSeeder;

  @Autowired private UserSeeder userSeeder;
  @Autowired private StudentSeeder studentSeeder;
  @Autowired private DeclaredSkillSeeder declaredSkillSeeder;

  private static List<UserEntity> users;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
    studentSeeder.seed(users);
    declaredSkillSeeder.seed();
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("a trace seeder");
    List<UserEntity> emptyUsers = List.of();

    BddLogger.when("seeding traces and there is no users");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> traceSeeder.seed(emptyUsers));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnTraces_withCorrectSizeAndUser() {
    BddLogger.given("a trace seeder");
    BddLogger.when("seeding traces with correct fileSize and user");
    List<TraceEntity> traces = traceSeeder.seed(users);

    BddLogger.then("it should return traces");
    assertNotNull(traces);
    assertFalse(traces.isEmpty());

    // Vérifie que chaque trace est associée à un utilisateur
    for (TraceEntity trace : traces) {
      assertNotNull(trace.getUser());
      assertTrue(
          users.stream().map(AvenirsBaseEntity::getId).toList().contains(trace.getUser().getId()));
    }
  }
}
