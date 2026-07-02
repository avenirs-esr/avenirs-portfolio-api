package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FakeActivityFile {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeActivityFile.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeActivityFile.class, FileDataGenerator.class);
  private final FileEntity activityFile;

  private FakeActivityFile(FileEntity activityFile) {
    this.activityFile = activityFile;
  }

  public static FakeActivityFile create(UUID activityId) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.values()));
    var id = dataGenerator.with("id").uuid();
    return new FakeActivityFile(
        FileEntity.of(
            id,
            fileType,
            EFileCategory.ACTIVITY_FILE,
            fileDataGenerator.with("file-name").fileName(fileType),
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            activityId,
            null,
            Instant.now(),
            Instant.now(),
            Instant.now()));
  }

  public static FakeActivityFile create(ActivityDraftEntity activity) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.values()));
    var id = dataGenerator.with("id").uuid();
    return new FakeActivityFile(
        FileEntity.of(
            id,
            fileType,
            EFileCategory.ACTIVITY_FILE,
            fileDataGenerator.with("file-name").fileName(fileType),
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            activity.getId(),
            null,
            Instant.now(),
            Instant.now(),
            Instant.now()));
  }

  public FileEntity toEntity() {
    return activityFile;
  }
}
