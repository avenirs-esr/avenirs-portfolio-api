package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.SkillLevelDatabaseRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
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
class SkillSeederTest {

  @Autowired private SkillSeeder skillSeeder;

  @Autowired private InstitutionSeeder institutionSeeder;

  @Autowired private SkillDatabaseRepository skillRepository;

  @Autowired private SkillLevelDatabaseRepository skillLevelRepository;

  @Autowired private ProgramSeeder programSeeder;

  private static List<ProgramEntity> programs;

  @BeforeAll
  void setUp() {
    programs = programSeeder.seed(institutionSeeder.seed());
  }

  @Test
  void seed_shouldThrowException_whenProgramsEmpty() {
    List<ProgramEntity> emptyPrograms = List.of();
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> skillSeeder.seed(emptyPrograms));
    assertTrue(exception.getMessage().contains("programs cannot be empty"));
  }

  @Test
  void seed_shouldReturnSkillLevels_withCorrectSize() {
    List<SkillLevelEntity> skillLevels = skillSeeder.seed(programs);

    assertNotNull(skillLevels);
    assertFalse(skillLevels.isEmpty());

    // Vérifie que le nombre total correspond à programs * SKILL_BY_PROGRAM * SKILL_LEVEL_BY_SKILL
    int expectedTotal =
        programs.size() * SeederConfig.SKILL_BY_PROGRAM * SeederConfig.SKILL_LEVEL_BY_SKILL;
    assertEquals(expectedTotal, skillLevels.size());

    // Vérifie que chaque skillLevel a bien une traduction ENGLISH
    skillLevels.forEach(
        skillLevel ->
            assertTrue(
                skillLevel.getTranslations().stream()
                    .anyMatch(t -> t.getLanguage().name().equals("ENGLISH"))));
  }

  @Test
  void seed_shouldCallRepositorySaveAllEntities() {
    SkillDatabaseRepository mockSkillRepo = mock(SkillDatabaseRepository.class);
    SkillLevelDatabaseRepository mockSkillLevelRepo = mock(SkillLevelDatabaseRepository.class);
    SkillSeeder seederWithMock = new SkillSeeder(mockSkillRepo, mockSkillLevelRepo);

    List<SkillLevelEntity> result = seederWithMock.seed(programs);

    verify(mockSkillRepo, times(1)).saveAllEntities(any());
    verify(mockSkillLevelRepo, times(1)).saveAllEntities(result);
  }
}
