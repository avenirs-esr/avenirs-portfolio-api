package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EFileType;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment extends AvenirsBaseModel {
  private final Trace trace;
  private final String name;
  private final EFileType attachmentType;
  private final long size;
  private final int version;
  private boolean isActiveVersion;
  private final Instant uploadedAt;
  private final String uri;

  private Attachment(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    super(id);
    this.trace = trace;
    this.name = name;
    this.attachmentType = attachmentType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uploadedAt = uploadedAt;
    this.uri = uri;
  }

  public static Attachment create(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri) {
    return new Attachment(
        id, trace, name, attachmentType, size, version, isActiveVersion, Instant.now(), uri);
  }

  public static Attachment toDomain(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    return new Attachment(
        id, trace, name, attachmentType, size, version, isActiveVersion, uploadedAt, uri);
  }
}
