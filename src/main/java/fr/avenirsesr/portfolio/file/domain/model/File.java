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
  private final String uri;
  private final User uploadedBy;
  private final Instant uploadedAt;
  private final boolean isRestricted;

  protected File(
      UUID id,
      String fileName,
      EFileType fileType,
      long size,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      boolean isRestricted,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.fileName = fileName;
    this.fileType = fileType;
    this.size = size;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
    this.isRestricted = isRestricted;
  }

  public static File create(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      String uri,
      User uploadedBy,
      boolean isRestricted) {
    return new File(
        id,
        fileName,
        fileType,
        size,
        uri,
        uploadedBy,
        Instant.now(),
        isRestricted,
        Instant.now(),
        Instant.now());
  }

  public static File toDomain(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      boolean isRestricted,
      Instant createdAt,
      Instant updatedAt) {
    return new File(
        id,
        fileName,
        fileType,
        size,
        uri,
        uploadedBy,
        uploadedAt,
        isRestricted,
        createdAt,
        updatedAt);
  }
}
