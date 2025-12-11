package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.AdditionalSkillSeeder;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.AdditionalSkillProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TraceSeederTest extends ContainerConfigurationTest {

  @Autowired private TraceSeeder traceSeeder;

  @Autowired private UserSeeder userSeeder;
  @Autowired private StudentSeeder studentSeeder;
  @Autowired private AdditionalSkillProgressSeeder additionalSkillProgressSeeder;
  @Autowired private AdditionalSkillSeeder additionalSkillSeeder;

  private static List<UserEntity> users;
  private static List<AdditionalSkillProgressEntity> additionalSkillProgresses;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
    List<StudentEntity> students = studentSeeder.seed(users);
    var additionalSkills = additionalSkillSeeder.seed();
    additionalSkillProgresses = additionalSkillProgressSeeder.seed(students, additionalSkills);
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("a trace seeder");
    List<UserEntity> emptyUsers = List.of();

    BddLogger.when("seeding traces and there is no users");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> traceSeeder.seed(emptyUsers, additionalSkillProgresses));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnTraces_withCorrectSizeAndUser() {
    BddLogger.given("a trace seeder");
    BddLogger.when("seeding traces with correct size and user");
    List<TraceEntity> traces = traceSeeder.seed(users, additionalSkillProgresses);

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
    TraceDatabaseRepository mockRepo = mock(TraceDatabaseRepository.class);
    TraceSeeder seederWithMock = new TraceSeeder(mockRepo);

    BddLogger.when("seeding traces");
    List<TraceEntity> result = seederWithMock.seed(users, additionalSkillProgresses);

    BddLogger.then("it should call repository and save all");
    verify(mockRepo, times(1)).saveAllEntities(any());
  }
}
