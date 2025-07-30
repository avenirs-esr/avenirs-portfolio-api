package fr.avenirsesr.portfolio.file.infrastructure.fixture;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeTraceAttachment;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.UUID;

public class TraceAttachmentFixture {
  private UUID id;
  private Trace trace;
  private String fileName;
  private EFileType fileType;
  private long size;
  private int version;
  private boolean isActiveVersion;
  private String uri;
  private User uploadedBy;
  private Instant uploadedAt;

  private TraceAttachmentFixture() {
    // Default: create a TraceFixture and use FakeTraceAttachment to generate default entity, then
    // map to fields
    Trace defaultTrace = TraceFixture.create().toModel();
    var entity =
        FakeTraceAttachment.of(
                FakeTrace.of(UserMapper.fromDomain(defaultTrace.getUser())).toEntity())
            .toEntity();
    var user = UserFixture.create().toModel();
    this.id = entity.getId();
    this.trace = TraceFixture.create().withUser(user).withId(entity.getTrace().getId()).toModel();
    this.fileName = entity.getName();
    this.fileType = entity.getFileType();
    this.size = entity.getSize();
    this.version = entity.getVersion();
    this.isActiveVersion = entity.isActiveVersion();
    this.uri = entity.getUri();
    this.uploadedBy = user;
    this.uploadedAt = entity.getUploadedAt();
  }

  public static TraceAttachmentFixture create() {
    return new TraceAttachmentFixture();
  }

  public TraceAttachmentFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public TraceAttachmentFixture withTrace(Trace trace) {
    this.trace = trace;
    return this;
  }

  public TraceAttachmentFixture withFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public TraceAttachmentFixture withFileType(EFileType fileType) {
    this.fileType = fileType;
    return this;
  }

  public TraceAttachmentFixture withSize(long size) {
    this.size = size;
    return this;
  }

  public TraceAttachmentFixture withVersion(int version) {
    this.version = version;
    return this;
  }

  public TraceAttachmentFixture withActiveVersion(boolean isActiveVersion) {
    this.isActiveVersion = isActiveVersion;
    return this;
  }

  public TraceAttachmentFixture withUri(String uri) {
    this.uri = uri;
    return this;
  }

  public TraceAttachmentFixture withUploadedBy(User uploadedBy) {
    this.uploadedBy = uploadedBy;
    return this;
  }

  public TraceAttachmentFixture withUploadedAt(Instant uploadedAt) {
    this.uploadedAt = uploadedAt;
    return this;
  }

  public TraceAttachment toModel() {
    return TraceAttachment.toDomain(
        id, trace, fileName, fileType, size, version, isActiveVersion, uri, uploadedBy, uploadedAt);
  }
}
