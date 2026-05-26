package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
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
  private final FileEntity userPhoto;

  private FakeUserPhoto(FileEntity userPhoto) {
    this.userPhoto = userPhoto;
  }

  public static FakeUserPhoto of(UserEntity user) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.PNG, EFileType.JPEG));
    var id = dataGenerator.with("id").uuid();
    return new FakeUserPhoto(
        FileEntity.of(
            id,
            fileType,
            dataGenerator
                .with("category")
                .pickIn(
                    List.of(
                        EFileCategory.STUDENT_COVER_PICTURE,
                        EFileCategory.STUDENT_PROFILE_PICTURE,
                        EFileCategory.STAFF_COVER_PICTURE,
                        EFileCategory.STAFF_PROFILE_PICTURE)),
            fileDataGenerator.with("file-name").fileName(fileType),
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            user.getId(),
            user,
            Instant.now(),
            Instant.now(),
            Instant.now()));
  }

  public FileEntity toEntity() {
    return userPhoto;
  }
}
