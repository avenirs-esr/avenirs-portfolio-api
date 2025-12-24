package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.UserPhotoMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeUserPhoto;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPhotoSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(UserPhotoSeeder.class, SharedDataGenerator.class);
  private final UserPhotoRepository userPhotoRepository;

  private List<UserPhotoEntity> generatePhotosOf(
      UserEntity user, EUserPhotoType type, EUserCategory userCategory, int maxNumber) {
    List<UserPhotoEntity> photos = new ArrayList<>();
    var nbOfProfileVersions = dataGenerator.with("random-number").number(1, maxNumber);
    for (int j = 1; j <= nbOfProfileVersions; j++) {
      photos.add(
          FakeUserPhoto.of(user, userCategory)
              .withVersion(j)
              .withIsActiveVersion(j == nbOfProfileVersions)
              .withUserPhotoType(type)
              .toEntity());
    }

    return photos;
  }

  @Transactional
  public List<UserPhotoEntity> seed(List<StudentEntity> students, List<TeacherEntity> teachers) {
    ValidationUtils.requireNonEmpty(students, "students cannot be empty");
    ValidationUtils.requireNonEmpty(teachers, "teachers cannot be empty");

    log.info("Seeding user photos...");

    List<UserPhotoEntity> userPhotoEntities = new ArrayList<>();
    for (StudentEntity student : students) {
      userPhotoEntities.addAll(
          generatePhotosOf(
              student.getUser(),
              EUserPhotoType.PROFILE,
              EUserCategory.STUDENT,
              SeederConfig.MAX_PROFILE_PHOTO_PER_USER));

      userPhotoEntities.addAll(
          generatePhotosOf(
              student.getUser(),
              EUserPhotoType.COVER,
              EUserCategory.STUDENT,
              SeederConfig.MAX_COVER_PHOTO_PER_USER));
    }

    for (TeacherEntity teacher : teachers) {
      if (dataGenerator.with("has-photo").bool()) {
        userPhotoEntities.addAll(
            generatePhotosOf(
                teacher.getUser(),
                EUserPhotoType.PROFILE,
                EUserCategory.TEACHER,
                SeederConfig.MAX_PROFILE_PHOTO_PER_USER));

        userPhotoEntities.addAll(
            generatePhotosOf(
                teacher.getUser(),
                EUserPhotoType.COVER,
                EUserCategory.TEACHER,
                SeederConfig.MAX_COVER_PHOTO_PER_USER));
      }
    }

    userPhotoRepository.saveAll(
        userPhotoEntities.stream().map(UserPhotoMapper.INSTANCE::toDomain).toList());
    log.info("✔ {} user photos created", userPhotoEntities.size());
    return userPhotoEntities;
  }
}
