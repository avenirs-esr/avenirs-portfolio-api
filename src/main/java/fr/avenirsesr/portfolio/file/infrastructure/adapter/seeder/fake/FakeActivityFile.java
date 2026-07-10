package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data.ActivityFileCreationData;
import java.util.List;
import java.util.UUID;

public class FakeActivityFile {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeActivityFile.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeActivityFile.class, FileDataGenerator.class);

  private FakeActivityFile() {}

  public static ActivityFileCreationData create(UUID activityId) {
    var fileType = dataGenerator.with("file-type").pickIn(List.of(EFileType.values()));
    return new ActivityFileCreationData(
        activityId,
        fileDataGenerator.with("file-name").fileName(fileType),
        fileType,
        dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()));
  }
}
