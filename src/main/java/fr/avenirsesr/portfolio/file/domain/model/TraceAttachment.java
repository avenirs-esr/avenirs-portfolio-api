package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
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
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    super(
        id,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        createdAt,
        updatedAt);
    this.trace = trace;
    this.name = name;
  }

  public static TraceAttachment create(
      UUID id,
      Trace trace,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy) {

    return new TraceAttachment(
        id,
        trace,
        name,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        Instant.now(),
        Instant.now(),
        Instant.now());
  }

  public static TraceAttachment toDomain(
      UUID id,
      Trace trace,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new TraceAttachment(
        id,
        trace,
        name,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        createdAt,
        updatedAt);
  }
}
