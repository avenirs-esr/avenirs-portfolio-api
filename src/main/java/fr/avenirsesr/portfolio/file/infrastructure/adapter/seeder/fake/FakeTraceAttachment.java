package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
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

  private final TraceAttachmentEntity attachment;

  private FakeTraceAttachment(TraceAttachmentEntity attachment) {
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
        TraceAttachmentEntity.of(
            id,
            trace,
            fileDataGenerator.with("filename").fileName(fileType),
            fileType,
            dataGenerator.with("fileSize").number((int) fileType.getSizeLimit().bytes()),
            1,
            true,
            "/workspace/app%s/%s.%s"
                .formatted(
                    FileStorageConstants.STORAGE_PATH,
                    FileStorageConstants.PLACEHOLDER_FILE_UUID,
                    fileType.name().toLowerCase()),
            trace.getUser(),
            trace.getCreatedAt(),
            trace.getCreatedAt(),
            trace.getUpdatedAt()));
  }

  public FakeTraceAttachment withIsActiveVersion(boolean isActiveVersion) {
    attachment.setActiveVersion(isActiveVersion);
    return this;
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

  public TraceAttachmentEntity toEntity() {
    return attachment;
  }
}
