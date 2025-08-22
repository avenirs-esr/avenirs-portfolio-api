package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
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
class StudentProgressSeederTest {

  @Autowired private StudentProgressSeeder studentProgressSeeder;
  @Autowired private StudentProgressDatabaseRepository studentProgressRepository;
  @Autowired private UserSeeder userSeeder;
  @Autowired private ProgramSeeder programSeeder;
  @Autowired private SkillSeeder skillSeeder;
  @Autowired private TrainingPathSeeder trainingPathSeeder;

  private static List<UserEntity> users;
  private static List<SkillLevelEntity> skillLevels;
  private static List<TrainingPathEntity> trainingPaths;

  @BeforeAll
  static void setUp(
      @Autowired UserSeeder userSeeder,
      @Autowired ProgramSeeder programSeeder,
      @Autowired SkillSeeder skillSeeder,
      @Autowired TrainingPathSeeder trainingPathSeeder,
      @Autowired InstitutionSeeder institutionSeeder) {

    // Seed des utilisateurs
    var savedUsers = userSeeder.seed();

    // Seed des institutions
    var savedInstitutions = institutionSeeder.seed();

    // Seed des programmes avec institutions
    var savedPrograms = programSeeder.seed(savedInstitutions);

    // Seed des skill levels avec les programmes
    var savedSkillLevels = skillSeeder.seed(savedPrograms);

    // Seed des training paths avec programmes et skill levels
    var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);

    // Filtre les utilisateurs qui ont des students
    users = savedUsers.stream().filter(u -> u.getStudent().isPresent()).toList();
    skillLevels = savedSkillLevels;
    trainingPaths = savedTrainingPaths;
  }

  @Test
  void seed_shouldThrowException_whenTrainingPathsEmpty() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(List.of(), users, skillLevels));
    assertTrue(exception.getMessage().contains("training paths cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(trainingPaths, List.of(), skillLevels));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenSkillLevelsEmpty() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> studentProgressSeeder.seed(trainingPaths, users, List.of()));
    assertTrue(exception.getMessage().contains("skill levels cannot be empty"));
  }

  @Test
  void seed_shouldReturnStudentProgress_withCorrectRelations() {
    List<StudentProgressEntity> result =
        studentProgressSeeder.seed(trainingPaths, users, skillLevels);

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
    StudentProgressDatabaseRepository mockRepo = mock(StudentProgressDatabaseRepository.class);
    StudentProgressSeeder seederWithMock = new StudentProgressSeeder(mockRepo);

    List<StudentProgressEntity> result = seederWithMock.seed(trainingPaths, users, skillLevels);

    verify(mockRepo, times(1)).saveAllEntities(result);
  }
}
