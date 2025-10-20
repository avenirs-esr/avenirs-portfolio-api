package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;

public class FakeUserPhoto {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeUserPhoto.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeUserPhoto.class, FileDataGenerator.class);
  private final UserPhotoEntity userPhoto;

  private FakeUserPhoto(UserPhotoEntity userPhoto) {
    this.userPhoto = userPhoto;
  }

  public static FakeUserPhoto of(UserEntity user, EUserCategory category) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.PNG, EFileType.JPEG));
    var id = dataGenerator.with("id").uuid();
    return new FakeUserPhoto(
        UserPhotoEntity.of(
            id,
            fileDataGenerator.with("file-name").fileName(EFileType.PNG),
            user,
            category,
            EUserPhotoType.PROFILE,
            fileType,
            dataGenerator.with("size").number((int) fileType.getSizeLimit().bytes()),
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
