package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.TeacherSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserPhotoSeederTest extends ContainerConfigurationTest {

  @Autowired private UserPhotoSeeder userPhotoSeeder;

  @Autowired private UserSeeder userSeeder;
  @Autowired private StudentSeeder studentSeeder;
  @Autowired private TeacherSeeder teacherSeeder;

  private List<UserEntity> users;
  private List<StudentEntity> students;
  private List<TeacherEntity> teachers;

  @BeforeAll
  void setUp() {
    // Seed des utilisateurs avec UserSeeder
    this.users = userSeeder.seed();
    this.students = studentSeeder.seed(users);
    this.teachers = teacherSeeder.seed(users);
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
  void seed_shouldThrowException_whenTeachersEmpty() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is no teacher");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class, () -> userPhotoSeeder.seed(students, List.of()));
    assertTrue(exception.getMessage().contains("teachers cannot be empty"));
  }

  @Test
  void seed_shouldReturnUserPhotos_forAllUsers() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is a list of users");
    List<UserPhotoEntity> photos = userPhotoSeeder.seed(students, teachers);

    BddLogger.then("it should return user photos for all users");
    assertNotNull(photos);
    assertFalse(photos.isEmpty());

    // Vérifie que chaque utilisateur a au moins une photo
    for (StudentEntity student : students) {
      boolean hasPhoto =
          photos.stream().anyMatch(photo -> photo.getUser().getId().equals(student.getId()));
      assertTrue(hasPhoto, "User " + student.getId() + " doit avoir au moins une photo");
    }

    // Vérifie les versions et types
    photos.forEach(
        photo -> {
          assertTrue(photo.getVersion() > 0);
          assertNotNull(photo.getUserPhotoType());
          assertNotNull(photo.getUserCategory());
        });
  }
}
