package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class File extends AvenirsBaseModel {
  private final EFileType fileType;
  private final String fileName;
  private final long size;
  private final int version;
  private final String uri;
  private final User uploadedBy;
  private final Instant uploadedAt;
  private final boolean isPublic;

  protected File(
      UUID id,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      boolean isPublic,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.fileName = fileName;
    this.fileType = fileType;
    this.size = size;
    this.version = version;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
    this.isPublic = isPublic;
  }

  public static File create(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      int version,
      String uri,
      User uploadedBy,
      boolean isPublic) {
    return new File(
        id,
        fileName,
        fileType,
        size,
        version,
        uri,
        uploadedBy,
        Instant.now(),
        isPublic,
        Instant.now(),
        Instant.now());
  }

  public static File toDomain(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      int version,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      boolean isPublic,
      Instant createdAt,
      Instant updatedAt) {
    return new File(
        id,
        fileName,
        fileType,
        size,
        version,
        uri,
        uploadedBy,
        uploadedAt,
        isPublic,
        createdAt,
        updatedAt);
  }
}
