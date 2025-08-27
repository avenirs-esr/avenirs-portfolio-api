package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.ProgramDatabaseRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ProgramSeederTest {

  @Autowired private ProgramSeeder programSeeder;
  @Autowired private ProgramDatabaseRepository programRepository;
  @Autowired private InstitutionSeeder institutionSeeder;

  private static List<InstitutionEntity> institutions;

  @BeforeAll
  void setUp() {
    // Seed des institutions
    institutions = institutionSeeder.seed();
  }

  @Test
  void seed_shouldThrowException_whenInstitutionsEmpty() {
    List<InstitutionEntity> emptyInstitutions = List.of();
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> programSeeder.seed(emptyInstitutions));
    assertTrue(exception.getMessage().contains("institutions cannot be empty"));
  }

  @Test
  void seed_shouldReturnPrograms_withCorrectSize() {
    List<ProgramEntity> programs = programSeeder.seed(institutions);

    assertNotNull(programs);
    assertFalse(programs.isEmpty());

    // Vérifie que le nombre total correspond à institutions * PROGRAM_BY_INSTITUTION
    int expectedTotal = institutions.size() * SeederConfig.PROGRAM_BY_INSTITUTION;
    assertEquals(expectedTotal, programs.size());

    // Vérifie que chaque programme a bien une institution associée
    programs.forEach(
        program -> {
          assertNotNull(program.getInstitution());
        });
  }

  @Test
  void seed_shouldIncludeAllTranslationsExceptFrench() {
    List<ProgramEntity> programs = programSeeder.seed(institutions);

    programs.forEach(
        program -> {
          assertTrue(program.getTranslations().stream().anyMatch(t -> t.getLanguage() != null));
        });
  }

  @Test
  void seed_shouldCallRepositorySaveAllEntities() {
    ProgramDatabaseRepository mockRepo = mock(ProgramDatabaseRepository.class);
    ProgramSeeder seederWithMock = new ProgramSeeder(mockRepo);

    List<ProgramEntity> result = seederWithMock.seed(institutions);
    verify(mockRepo, times(1)).saveAllEntities(result);
  }
}
