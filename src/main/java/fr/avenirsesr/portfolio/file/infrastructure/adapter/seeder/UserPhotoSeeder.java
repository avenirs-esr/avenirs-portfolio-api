package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.UserPhotoMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeUserPhoto;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPhotoSeeder {
  private static final Faker faker = new FakerProvider().call();
  private final UserPhotoRepository userPhotoRepository;

  private List<UserPhotoEntity> generatePhotosOf(
      UserEntity user, EUserPhotoType type, EUserCategory userCategory, int maxNumber) {
    List<UserPhotoEntity> photos = new ArrayList<>();
    var nbOfProfileVersions = faker.random().nextInt(1, maxNumber);
    for (int j = 1; j <= nbOfProfileVersions; j++) {
      photos.add(
          FakeUserPhoto.of(user)
              .withVersion(j)
              .withIsActiveVersion(j == nbOfProfileVersions)
              .withUserPhotoType(type)
              .withUserCategory(userCategory)
              .toEntity());
    }

    return photos;
  }

  public List<UserPhotoEntity> seed(List<UserEntity> users) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding user photos...");

    List<UserPhotoEntity> userPhotoEntities = new ArrayList<>();
    for (UserEntity user : users) {
      userPhotoEntities.addAll(
          generatePhotosOf(
              user,
              EUserPhotoType.PROFILE,
              user.getStudent().isPresent() ? EUserCategory.STUDENT : EUserCategory.TEACHER,
              SeederConfig.MAX_PROFILE_PHOTO_PER_USER));

      userPhotoEntities.addAll(
          generatePhotosOf(
              user,
              EUserPhotoType.COVER,
              user.getStudent().isPresent() ? EUserCategory.STUDENT : EUserCategory.TEACHER,
              SeederConfig.MAX_COVER_PHOTO_PER_USER));

      if (user.getStudent().isPresent() && user.getTeacher().isPresent()) {
        if (faker.random().nextBoolean()) {
          userPhotoEntities.addAll(
              generatePhotosOf(
                  user,
                  EUserPhotoType.PROFILE,
                  EUserCategory.TEACHER,
                  SeederConfig.MAX_PROFILE_PHOTO_PER_USER));

          userPhotoEntities.addAll(
              generatePhotosOf(
                  user,
                  EUserPhotoType.COVER,
                  EUserCategory.TEACHER,
                  SeederConfig.MAX_COVER_PHOTO_PER_USER));
        }
      }
    }

    userPhotoRepository.saveAll(userPhotoEntities.stream().map(UserPhotoMapper::toDomain).toList());
    log.info("✔ {} user photos created", userPhotoEntities.size());
    return userPhotoEntities;
  }
}
