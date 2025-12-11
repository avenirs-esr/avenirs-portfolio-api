package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillLevelDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.TrainingPathDatabaseRepository;
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
class TrainingPathSeederTest {

  @Autowired private TrainingPathSeeder trainingPathSeeder;
  @Autowired private TrainingPathDatabaseRepository trainingPathRepository;
  @Autowired private SkillLevelDatabaseRepository skillLevelRepository;
  @Autowired private ProgramSeeder programSeeder;
  @Autowired private SkillSeeder skillSeeder;
  @Autowired private InstitutionSeeder institutionSeeder;

  private static List<ProgramEntity> programs;
  private static List<SkillLevelEntity> skillLevels;

  @BeforeAll
  void setUp() {
    var savedInstitutions = institutionSeeder.seed();
    programs = programSeeder.seed(savedInstitutions);
    skillLevels = skillSeeder.seed(programs);
  }

  @Test
  void seed_shouldThrowException_whenProgramsEmpty() {
    BddLogger.given("a training path seeder");
    BddLogger.when("there is no programs");
    BddLogger.then("it should throw IllegalArgumentException");
    List<ProgramEntity> emptyPrograms = List.of();
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> trainingPathSeeder.seed(emptyPrograms, skillLevels));
    assertTrue(exception.getMessage().contains("programs cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenSkillLevelsEmpty() {
    BddLogger.given("a training path seeder");
    BddLogger.when("there is no skill levels");
    List<SkillLevelEntity> emptySkills = List.of();

    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainingPathSeeder.seed(programs, emptySkills));
    assertTrue(exception.getMessage().contains("skills cannot be empty"));
  }
}
