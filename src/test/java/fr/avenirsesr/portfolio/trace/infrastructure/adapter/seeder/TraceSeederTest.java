package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TraceSeederTest {

  @Autowired private TraceSeeder traceSeeder;

  @Autowired private UserSeeder userSeeder;

  private static List<UserEntity> users;

  @BeforeAll
  static void setUp(@Autowired UserSeeder userSeeder) {
    users = userSeeder.seed();
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    List<UserEntity> emptyUsers = List.of();
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> traceSeeder.seed(emptyUsers));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnTraces_withCorrectSizeAndUser() {
    List<TraceEntity> traces = traceSeeder.seed(users);

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
    TraceRepository mockRepo = mock(TraceRepository.class);
    TraceSeeder seederWithMock = new TraceSeeder(mockRepo);

    List<TraceEntity> result = seederWithMock.seed(users);

    verify(mockRepo, times(1)).saveAll(any());
  }
}
