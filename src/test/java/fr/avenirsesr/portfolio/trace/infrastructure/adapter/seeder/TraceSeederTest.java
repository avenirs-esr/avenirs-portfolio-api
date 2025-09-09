package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.testutils.BddLogger;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TraceSeederTest {

  @Autowired private TraceSeeder traceSeeder;

  @Autowired private UserSeeder userSeeder;

  private static List<UserEntity> users;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
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
    BddLogger.when("seeding traces with correct size and user");
    List<TraceEntity> traces = traceSeeder.seed(users);

    BddLogger.then("it should return traces");
    assertNotNull(traces);
    assertFalse(traces.isEmpty());

    // Vérifie que chaque trace est associée à un utilisateur
    for (TraceEntity trace : traces) {
      assertNotNull(trace.getUser());
      assertTrue(users.contains(trace.getUser()));
    }
  }

  @Test
  void seed_shouldCallRepositorySaveAll() {
    BddLogger.given("a trace seeder");
    TraceRepository mockRepo = mock(TraceRepository.class);
    TraceSeeder seederWithMock = new TraceSeeder(mockRepo);

    BddLogger.when("seeding traces");
    List<TraceEntity> result = seederWithMock.seed(users);

    BddLogger.then("it should call repository and save all");
    verify(mockRepo, times(1)).saveAll(any());
  }
}
