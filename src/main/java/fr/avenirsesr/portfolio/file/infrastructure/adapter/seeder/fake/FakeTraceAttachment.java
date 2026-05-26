package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.time.Instant;
import java.util.List;

public class FakeTraceAttachment {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeTraceAttachment.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<FileDataGenerator> fileDataGenerator =
      new DataGeneratorProvider<FileDataGenerator>()
          .init(FakeTraceAttachment.class, FileDataGenerator.class);

  private final FileEntity attachment;

  private FakeTraceAttachment(FileEntity attachment) {
    this.attachment = attachment;
  }

  public static FakeTraceAttachment of(TraceEntity trace) {
    var fileType =
        dataGenerator
            .with("file-type")
            .pickIn(
                List.of(
                    EFileType.PDF,
                    EFileType.PNG,
                    EFileType.JPEG,
                    EFileType.MP4,
                    EFileType.TXT,
                    EFileType.PPTX,
                    EFileType.CSV,
                    EFileType.DOC,
                    EFileType.XLS));
    var id = dataGenerator.with("id").uuid();
    return new FakeTraceAttachment(
        FileEntity.of(
            id,
            fileType,
            EFileCategory.TRACE_ATTACHEMENT,
            fileDataGenerator.with("file-name").fileName(fileType),
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            trace.getId(),
            trace.getUser(),
            trace.getCreatedAt(),
            trace.getCreatedAt(),
            trace.getUpdatedAt()));
  }

  public FakeTraceAttachment withVersion(int version) {
    attachment.setVersion(version);
    return this;
  }

  public FakeTraceAttachment withUploadedAt(Instant uploadedAt) {
    attachment.setUploadedAt(uploadedAt);
    return this;
  }

  public FakeTraceAttachment withUri(String uri) {
    attachment.setUri(uri);
    return this;
  }

  public FakeTraceAttachment withSize(long size) {
    attachment.setSize(size);
    return this;
  }

  public FakeTraceAttachment withFileType(EFileType type) {
    attachment.setFileType(type);
    return this;
  }

  public FileEntity toEntity() {
    return attachment;
  }
}
