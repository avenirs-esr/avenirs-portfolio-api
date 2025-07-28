package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraceAttachment extends File {
  private final Trace trace;
  private final String name;

  private TraceAttachment(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    super(id, attachmentType, size, version, isActiveVersion, uploadedAt, uri);
    this.trace = trace;
    this.name = name;
  }

  public static TraceAttachment create(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri) {
    return new TraceAttachment(
        id, trace, name, attachmentType, size, version, isActiveVersion, Instant.now(), uri);
  }

  public static TraceAttachment toDomain(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    return new TraceAttachment(
        id, trace, name, attachmentType, size, version, isActiveVersion, uploadedAt, uri);
  }
}
