package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
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
      String uri,
      User uploadedBy,
      Instant uploadedAt) {
    super(id, attachmentType, size, version, isActiveVersion, uri, uploadedBy, uploadedAt);
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
      String uri,
      User uploadedBy) {

    return new TraceAttachment(
        id,
        trace,
        name,
        attachmentType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        Instant.now());
  }

  public static TraceAttachment toDomain(
      UUID id,
      Trace trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt) {
    return new TraceAttachment(
        id,
        trace,
        name,
        attachmentType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt);
  }
}
