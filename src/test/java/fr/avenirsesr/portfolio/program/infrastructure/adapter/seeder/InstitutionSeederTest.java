package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.InstitutionDatabaseRepository;
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
class InstitutionSeederTest {

  @Autowired private InstitutionSeeder institutionSeeder;
  @Autowired private InstitutionDatabaseRepository institutionRepository;

  private static List<InstitutionEntity> institutions;

  @BeforeAll
  void setUp() {
    institutions = institutionSeeder.seed();
  }

  @Test
  void seed_shouldReturnNonEmptyList() {
    assertNotNull(institutions);
    assertFalse(institutions.isEmpty());

    int expectedTotal =
        SeederConfig.INSTITUTIONS_NB_OF_APC
            + SeederConfig.INSTITUTIONS_NB_OF_LIFE_PROJECT
            + SeederConfig.INSTITUTIONS_NB_OF_BOTH;
    assertEquals(expectedTotal, institutions.size());
  }

  @Test
  void seed_shouldIncludeAllTranslations() {
    for (InstitutionEntity inst : institutions) {
      assertTrue(inst.getTranslations().stream().anyMatch(t -> t.getLanguage() != null));
    }
  }

  @Test
  void seed_shouldCallRepositorySaveAllEntities() {
    InstitutionDatabaseRepository mockRepo = mock(InstitutionDatabaseRepository.class);
    InstitutionSeeder seederWithMock = new InstitutionSeeder(mockRepo);

    List<InstitutionEntity> result = seederWithMock.seed();
    verify(mockRepo, times(1)).saveAllEntities(result);
  }
}
