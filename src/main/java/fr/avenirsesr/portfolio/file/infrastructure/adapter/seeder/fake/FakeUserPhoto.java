package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeUserPhoto {
  private static final Faker faker = new FakerProvider().call();
  private final UserPhotoEntity userPhoto;

  private FakeUserPhoto(UserPhotoEntity userPhoto) {
    this.userPhoto = userPhoto;
  }

  public static FakeUserPhoto of(UserEntity user) {
    var fileType = faker.options().option(EFileType.PNG, EFileType.JPEG);
    var id = UUID.fromString(faker.internet().uuid());
    return new FakeUserPhoto(
        UserPhotoEntity.of(
            id,
            user,
            user.getStudent().isPresent() ? EUserCategory.STUDENT : EUserCategory.TEACHER,
            EUserPhotoType.PROFILE,
            fileType,
            faker.random().nextLong(fileType.getSizeLimit().bytes()),
            1,
            true,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            user,
            Instant.now()));
  }

  public FakeUserPhoto withUserCategory(EUserCategory category) {
    userPhoto.setUserCategory(category);
    return this;
  }

  public FakeUserPhoto withUserPhotoType(EUserPhotoType type) {
    userPhoto.setUserPhotoType(type);
    return this;
  }

  public FakeUserPhoto withIsActiveVersion(boolean isActiveVersion) {
    userPhoto.setActiveVersion(isActiveVersion);
    return this;
  }

  public FakeUserPhoto withVersion(int version) {
    userPhoto.setVersion(version);
    return this;
  }

  public FakeUserPhoto withUploadedAt(Instant uploadedAt) {
    userPhoto.setUploadedAt(uploadedAt);
    return this;
  }

  public FakeUserPhoto withUri(String uri) {
    userPhoto.setUri(uri);
    return this;
  }

  public FakeUserPhoto withSize(long size) {
    userPhoto.setSize(size);
    return this;
  }

  public FakeUserPhoto withFileType(EFileType type) {
    userPhoto.setFileType(type);
    return this;
  }

  public UserPhotoEntity toEntity() {
    return userPhoto;
  }
}
