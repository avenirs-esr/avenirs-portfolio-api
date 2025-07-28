package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.configuration.FileStorageConfig;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeTraceAttachment {
  private static final Faker faker = new FakerProvider().call();
  private final TraceAttachmentEntity attachment;

  private FakeTraceAttachment(TraceAttachmentEntity attachment) {
    this.attachment = attachment;
  }

  public static FakeTraceAttachment of(TraceEntity trace) {
    var fileType =
        faker
            .options()
            .option(
                EFileType.PDF,
                EFileType.PNG,
                EFileType.JPEG,
                EFileType.MP4,
                EFileType.TXT,
                EFileType.PPTX,
                EFileType.CSV,
                EFileType.DOC,
                EFileType.XLS);
    var id = UUID.fromString(faker.internet().uuid());
    return new FakeTraceAttachment(
        TraceAttachmentEntity.of(
            id,
            trace,
            faker.file().fileName("", null, fileType.name().toLowerCase(), ""),
            fileType,
            faker.random().nextLong(fileType.getSizeLimit().bytes()),
            1,
            true,
            trace.getCreatedAt(),
            "%s/traces/%s".formatted(FileStorageConfig.BASE_URL, id)));
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

  public FakeTraceAttachment withAttachmentType(EFileType type) {
    attachment.setAttachmentType(type);
    return this;
  }

  public TraceAttachmentEntity toEntity() {
    return attachment;
  }
}
