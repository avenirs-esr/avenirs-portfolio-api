package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
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
class UserPhotoSeederTest {

  @Autowired private UserPhotoSeeder userPhotoSeeder;

  @Autowired private UserSeeder userSeeder;

  private List<UserEntity> users;

  @BeforeAll
  void setUp() {
    // Seed des utilisateurs avec UserSeeder
    this.users = userSeeder.seed();
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is no users");
    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> userPhotoSeeder.seed(List.of()));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnUserPhotos_forAllUsers() {
    BddLogger.given("a user photo seeder");
    BddLogger.when("there is a list of users");
    List<UserPhotoEntity> photos = userPhotoSeeder.seed(users);

    BddLogger.then("it should return user photos for all users");
    assertNotNull(photos);
    assertFalse(photos.isEmpty());

    // Vérifie que chaque utilisateur a au moins une photo
    for (UserEntity user : users) {
      boolean hasPhoto =
          photos.stream().anyMatch(photo -> photo.getUser().getId().equals(user.getId()));
      assertTrue(hasPhoto, "User " + user.getId() + " doit avoir au moins une photo");
    }

    // Vérifie les versions et types
    photos.forEach(
        photo -> {
          assertTrue(photo.getVersion() > 0);
          assertNotNull(photo.getUserPhotoType());
          assertNotNull(photo.getUserCategory());
        });
  }

  @Test
  void seed_shouldRespectMaxNumberConstraints() {
    BddLogger.given("a user photo seeder");
    List<UserPhotoEntity> photos = userPhotoSeeder.seed(users);

    BddLogger.when("there is a list of users");
    BddLogger.then("it should respect max number constraints");
    users.forEach(
        user -> {
          long profileCount =
              photos.stream()
                  .filter(photo -> photo.getUser().getId().equals(user.getId()))
                  .filter(photo -> photo.getUserPhotoType().equals(EUserPhotoType.PROFILE))
                  .count();
          long coverCount =
              photos.stream()
                  .filter(photo -> photo.getUser().getId().equals(user.getId()))
                  .filter(photo -> photo.getUserPhotoType().equals(EUserPhotoType.COVER))
                  .count();

          if (user.getStudent().isPresent()) {
            if (user.getTeacher().isPresent()) {
              assertTrue(profileCount <= SeederConfig.MAX_PROFILE_PHOTO_PER_USER * 2);
              assertTrue(coverCount <= SeederConfig.MAX_COVER_PHOTO_PER_USER * 2);
            } else {
              assertTrue(profileCount <= SeederConfig.MAX_PROFILE_PHOTO_PER_USER);
              assertTrue(coverCount <= SeederConfig.MAX_COVER_PHOTO_PER_USER);
            }
          }
        });
  }
}
