package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class File extends AvenirsBaseModel {
  private final EFileType fileType;
  private final EFileCategory fileCategory;
  private final String fileName;
  private final long size;
  private final int version;
  private boolean isActiveVersion;
  private final String uri;
  private final User uploadedBy;
  private final Instant uploadedAt;
  private UUID elementId;

  protected File(
      UUID id,
      UUID elementId,
      EFileCategory fileCategory,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.elementId = elementId;
    this.fileCategory = fileCategory;
    this.fileName = fileName;
    this.fileType = fileType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
  }

  public static File create(
      UUID id,
      UUID elementId,
      EFileCategory fileCategory,
      EFileType fileType,
      String fileName,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy) {
    return new File(
        id,
        elementId,
        fileCategory,
        fileName,
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

  public static File toDomain(
      UUID id,
      UUID elementId,
      EFileType fileType,
      EFileCategory fileCategory,
      String fileName,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new File(
        id,
        elementId,
        fileCategory,
        fileName,
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
