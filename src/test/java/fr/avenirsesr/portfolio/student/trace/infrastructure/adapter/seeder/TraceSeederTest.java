package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
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

  private static List<StudentEntity> students;

  @BeforeAll
  void setUp() {
    var users = userSeeder.seed();
    students = studentSeeder.seed(users);
    declaredSkillSeeder.seed();
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("a trace seeder");
    List<StudentEntity> emptyStudents = List.of();

    BddLogger.when("seeding traces and there is no students");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> traceSeeder.seed(emptyStudents));
    assertTrue(exception.getMessage().contains("students cannot be empty"));
  }

  @Test
  void seed_shouldReturnTraces_withCorrectSizeAndUser() {
    BddLogger.given("a trace seeder");
    BddLogger.when("seeding traces with correct file size and user");
    List<TraceEntity> traces = traceSeeder.seed(students);

    BddLogger.then("it should return traces");
    assertNotNull(traces);
    assertFalse(traces.isEmpty());

    for (TraceEntity trace : traces) {
      assertNotNull(trace.getStudent());
      assertTrue(
          students.stream()
              .map(AvenirsBaseEntity::getId)
              .toList()
              .contains(trace.getStudent().getId()));
    }
  }
}
