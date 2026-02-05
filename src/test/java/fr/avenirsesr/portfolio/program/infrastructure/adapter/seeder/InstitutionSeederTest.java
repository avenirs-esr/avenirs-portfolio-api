package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.repository.InstitutionDatabaseRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InstitutionSeederTest extends ContainerConfigurationTest {

  @Autowired private InstitutionSeeder institutionSeeder;
  @Autowired private InstitutionDatabaseRepository institutionRepository;

  private static List<InstitutionEntity> institutions;

  @BeforeAll
  void setUp() {
    institutions = institutionSeeder.seed();
  }

  @Test
  void seed_shouldReturnNonEmptyList() {
    BddLogger.given("an institution seeder");
    BddLogger.when("seeding institutions");
    BddLogger.then("it should return a non empty list");
    assertNotNull(institutions);
    assertFalse(institutions.isEmpty());

    assertEquals(1, institutions.size());
  }

  @Test
  void seed_shouldIncludeAllTranslations() {
    BddLogger.given("an institution seeder");
    BddLogger.when("seeding institutions");
    BddLogger.then("it should include all translations");
    for (InstitutionEntity inst : institutions) {
      assertTrue(inst.getTranslations().stream().anyMatch(t -> t.getLanguage() != null));
    }
  }
}
