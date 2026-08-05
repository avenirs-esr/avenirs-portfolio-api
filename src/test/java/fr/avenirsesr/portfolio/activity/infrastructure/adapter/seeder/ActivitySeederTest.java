package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.ActivitySeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StaffSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class ActivitySeederTest extends ContainerConfigurationTest {

  @Autowired private UserSeeder userSeeder;
  @Autowired private StaffSeeder staffSeeder;
  @Autowired private ActivitySeeder activitySeeder;

  private static List<UserEntity> users;
  private static List<StaffEntity> savedStaffs;
  private static List<ActivityEntity> activities;

  @BeforeAll
  void setUp() {
    users = userSeeder.seed();
    savedStaffs = staffSeeder.seed(users);
    activities = activitySeeder.seed(savedStaffs.getFirst());
  }

  @Test
  void seed_shouldReturnNonEmptyActivitiesList() {
    BddLogger.given("an activity seeder");
    BddLogger.when("seeding activities");
    BddLogger.then("it should return activities");
    assertNotNull(activities);
    assertFalse(activities.isEmpty());
  }
}
