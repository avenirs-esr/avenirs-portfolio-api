package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillLevelDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.TrainingPathDatabaseRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
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
  static void setUp(
      @Autowired ProgramSeeder programSeeder,
      @Autowired SkillSeeder skillSeeder,
      @Autowired InstitutionSeeder institutionSeeder) {

    var savedInstitutions = institutionSeeder.seed();
    programs = programSeeder.seed(savedInstitutions);
    skillLevels = skillSeeder.seed(programs);
  }

  @Test
  void seed_shouldThrowException_whenProgramsEmpty() {
    List<ProgramEntity> emptyPrograms = List.of();
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> trainingPathSeeder.seed(emptyPrograms, skillLevels));
    assertTrue(exception.getMessage().contains("programs cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenSkillLevelsEmpty() {
    List<SkillLevelEntity> emptySkills = List.of();
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainingPathSeeder.seed(programs, emptySkills));
    assertTrue(exception.getMessage().contains("skills cannot be empty"));
  }
}
