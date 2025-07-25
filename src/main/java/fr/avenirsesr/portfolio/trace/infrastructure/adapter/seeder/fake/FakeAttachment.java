package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.configuration.FileStorageConfig;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.AttachmentEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.time.Instant;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeAttachment {
  private static final Faker faker = new FakerProvider().call();
  private final AttachmentEntity attachment;

  private FakeAttachment(AttachmentEntity attachment) {
    this.attachment = attachment;
  }

  public static FakeAttachment of(TraceEntity trace) {
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
    return new FakeAttachment(
        AttachmentEntity.of(
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

  public FakeAttachment withIsActiveVersion(boolean isActiveVersion) {
    attachment.setActiveVersion(isActiveVersion);
    return this;
  }

  public FakeAttachment withVersion(int version) {
    attachment.setVersion(version);
    return this;
  }

  public FakeAttachment withUploadedAt(Instant uploadedAt) {
    attachment.setUploadedAt(uploadedAt);
    return this;
  }

  public FakeAttachment withUri(String uri) {
    attachment.setUri(uri);
    return this;
  }

  public FakeAttachment withSize(long size) {
    attachment.setSize(size);
    return this;
  }

  public FakeAttachment withAttachmentType(EFileType type) {
    attachment.setAttachmentType(type);
    return this;
  }

  public AttachmentEntity toEntity() {
    return attachment;
  }
}
