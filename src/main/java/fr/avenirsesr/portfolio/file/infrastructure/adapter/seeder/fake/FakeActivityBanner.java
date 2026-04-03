package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import java.time.Instant;
import java.util.List;

public class FakeActivityBanner {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeUserPhoto.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeUserPhoto.class, FileDataGenerator.class);
  private final ActivityBannerEntity activityBanner;

  private FakeActivityBanner(ActivityBannerEntity activityBanner) {
    this.activityBanner = activityBanner;
  }

  public static FakeActivityBanner create(ActivityEntity activity) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.PNG, EFileType.JPEG));
    var id = dataGenerator.with("id").uuid();
    return new FakeActivityBanner(
        ActivityBannerEntity.of(
            id,
            activity,
            fileDataGenerator.with("file-name").fileName(fileType),
            fileType,
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            true,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            null,
            Instant.now()));
  }

  public ActivityBannerEntity toEntity() {
    return activityBanner;
  }
}
