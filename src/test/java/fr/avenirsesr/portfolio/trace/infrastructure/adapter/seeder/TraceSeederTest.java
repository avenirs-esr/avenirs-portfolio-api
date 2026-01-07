package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder.DeclaredSkillProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
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
  @Autowired private DeclaredSkillProgressSeeder declaredSkillProgressSeeder;
  @Autowired private DeclaredSkillSeeder declaredSkillSeeder;

  private static List<UserEntity> users;
  private static List<DeclaredSkillProgressEntity> declaredSkillProgresses;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
    List<StudentEntity> students = studentSeeder.seed(users);
    var declaredSkills = declaredSkillSeeder.seed();
    declaredSkillProgresses = declaredSkillProgressSeeder.seed(students, declaredSkills);
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
            () -> traceSeeder.seed(emptyUsers, declaredSkillProgresses));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnTraces_withCorrectSizeAndUser() {
    BddLogger.given("a trace seeder");
    BddLogger.when("seeding traces with correct size and user");
    List<TraceEntity> traces = traceSeeder.seed(users, declaredSkillProgresses);

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
