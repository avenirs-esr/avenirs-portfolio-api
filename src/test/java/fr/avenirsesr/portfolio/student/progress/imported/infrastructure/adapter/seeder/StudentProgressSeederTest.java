package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StudentProgressSeederTest extends ContainerConfigurationTest {

  @Autowired private StudentProgressSeeder studentProgressSeeder;
  @Autowired private StudentProgressDatabaseRepository studentProgressRepository;
  @Autowired private UserSeeder userSeeder;
  @Autowired private StudentSeeder studentSeeder;
  @Autowired private InstitutionSeeder institutionSeeder;
  @Autowired private ProgramSeeder programSeeder;
  @Autowired private SkillSeeder skillSeeder;
  @Autowired private TrainingPathSeeder trainingPathSeeder;

  private static List<StudentEntity> students;
  private static List<SkillLevelEntity> skillLevels;
  private static List<TrainingPathEntity> trainingPaths;

  @BeforeAll
  void setUp() {

    // Seed des utilisateurs
    var savedUsers = userSeeder.seed();

    // Seed des students
    students = studentSeeder.seed(savedUsers);

    // Seed des institutions
    var savedInstitutions = institutionSeeder.seed();

    // Seed des programmes avec institutions
    var savedPrograms = programSeeder.seed(savedInstitutions);

    // Seed des skill levels avec les programmes
    var savedSkillLevels = skillSeeder.seed(savedPrograms);

    // Seed des training paths avec programmes et skill levels
    var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);

    // Filtre les utilisateurs qui ont des students
    skillLevels = savedSkillLevels;
    trainingPaths = savedTrainingPaths;
  }

  @Test
  void seed_shouldThrowException_whenTrainingPathsEmpty() {
    BddLogger.given("a student progress seeder");
    BddLogger.when("seeding student progress and there is no training paths");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(List.of(), students, skillLevels));
    assertTrue(exception.getMessage().contains("training paths cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("a student progress seeder");
    BddLogger.when("seeding student progress and there is no users");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(trainingPaths, List.of(), skillLevels));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenSkillLevelsEmpty() {
    BddLogger.given("a student progress seeder");
    BddLogger.when("seeding student progress and there is no skill levels");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(trainingPaths, students, List.of()));
    assertTrue(exception.getMessage().contains("skill levels cannot be empty"));
  }

  @Test
  void seed_shouldReturnStudentProgress_withCorrectRelations() {
    BddLogger.given("a student progress seeder");
    BddLogger.when("seeding student progress");
    List<StudentProgressEntity> result =
        studentProgressSeeder.seed(trainingPaths, students, skillLevels);

    BddLogger.then("it should return student progresses");
    assertNotNull(result);
    assertFalse(result.isEmpty());

    // Vérifie que chaque StudentProgress a bien un TrainingPath et des SkillLevels
    for (StudentProgressEntity sp : result) {
      assertNotNull(sp.getTrainingPath());
      assertNotNull(sp.getSkillLevels());
      assertFalse(sp.getSkillLevels().isEmpty());
      assertNotNull(sp.getStartDate());
    }
  }

  @Test
  void seed_shouldCallRepositorySaveAllEntities() {
    BddLogger.given("a student progress seeder");
    StudentProgressDatabaseRepository mockRepo = mock(StudentProgressDatabaseRepository.class);
    StudentProgressSeeder seederWithMock = new StudentProgressSeeder(mockRepo);

    BddLogger.when("seeding student progress");
    List<StudentProgressEntity> result = seederWithMock.seed(trainingPaths, students, skillLevels);

    BddLogger.then("it should call repository and save all entities");
    verify(mockRepo, times(1)).saveAllEntities(result);
  }
}
