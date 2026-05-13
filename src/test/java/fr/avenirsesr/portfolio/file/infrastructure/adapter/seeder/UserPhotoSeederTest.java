package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StaffSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserPhotoSeederTest extends ContainerConfigurationTest {

  @Autowired private UserPhotoSeeder userPhotoSeeder;

  @Autowired private UserSeeder userSeeder;
  @Autowired private StudentSeeder studentSeeder;
  @Autowired private StaffSeeder staffSeeder;

  private List<UserEntity> users;
  private List<StudentEntity> students;
  private List<StaffEntity> staffs;

  @BeforeAll
  void setUp() {
    // Seed des utilisateurs avec UserSeeder
    this.users = userSeeder.seed();
    this.staffs = staffSeeder.seed(users);
    this.students = studentSeeder.seed(users);
  }

  @Test
  void seed_shouldThrowException_whenStudentsEmpty() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is no student");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> userPhotoSeeder.seed(List.of(), List.of()));
    assertTrue(exception.getMessage().contains("students cannot be empty"));
  }

  @Test
  void seed_shouldThrowException_whenStaffsEmpty() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is no staff");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> userPhotoSeeder.seed(students, List.of()));
    assertTrue(exception.getMessage().contains("staffs cannot be empty"));
  }
}
