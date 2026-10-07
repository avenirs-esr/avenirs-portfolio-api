package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.staff.activity.domain.model.SystemActivities;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository.ActivityJpaRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.SystemActivitySeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SystemActivitySeederTest extends ContainerConfigurationTest {

  @Autowired private SystemActivitySeeder systemActivitySeeder;
  @Autowired private ActivityRepository activityRepository;
  @Autowired private ActivityJpaRepository activityJpaRepository;

  @BeforeEach
  void removeSystemActivity() {
    activityJpaRepository.deleteById(SystemActivities.DISCOVERY_COFOLIO_ID);
  }

  @Test
  void seedAlone_shouldCreatePublishedActivityWithAuthorBannerAndAttachment() {
    BddLogger.given("no discovery activity and a system activity seeder");
    BddLogger.when("seeding system activities in insert-only mode");
    var processed = systemActivitySeeder.seedAlone(ESeedMode.INSERT_ONLY);

    BddLogger.then(
        "the activity exists with its stable id, cofadmin as author, banner, attachment and"
            + " status");
    assertEquals(1, processed);
    var activity = activityRepository.findById(SystemActivities.DISCOVERY_COFOLIO_ID).orElseThrow();
    assertEquals(
        "Découvrir mon CoFolio, ajouter et associer mes premiers éléments", activity.getTitle());
    assertEquals("ADMIN", activity.getAuthor().getUser().getLastName());
    assertEquals(-1, activity.getTraceAllowedAssociations());
    assertEquals(0, activity.getFeedbackAllowedIterations());
    assertTrue(activity.isEnableReflection());
    assertEquals(1, activity.getFiles().size());
    assertEquals(EActivityStatus.PUBLISHED, activity.getStatus());
    assertTrue(activity.getBanner().isPresent());
  }

  @Test
  void seedAlone_shouldNotDuplicateNorReplaceBannerWhenAlreadyPresent() {
    BddLogger.given("an already seeded discovery activity");
    systemActivitySeeder.seedAlone(ESeedMode.INSERT_ONLY);
    var bannerId =
        activityRepository
            .findById(SystemActivities.DISCOVERY_COFOLIO_ID)
            .orElseThrow()
            .getBanner()
            .orElseThrow()
            .getId();

    BddLogger.when("seeding again in insert-only mode");
    var processed = systemActivitySeeder.seedAlone(ESeedMode.INSERT_ONLY);

    BddLogger.then("nothing is created and the banner is unchanged");
    assertEquals(0, processed);
    var banner =
        activityRepository
            .findById(SystemActivities.DISCOVERY_COFOLIO_ID)
            .orElseThrow()
            .getBanner()
            .orElseThrow();
    assertEquals(bannerId, banner.getId());
  }
}
