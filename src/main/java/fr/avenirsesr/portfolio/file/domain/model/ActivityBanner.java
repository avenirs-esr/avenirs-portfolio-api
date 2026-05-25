package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityBanner extends File {
  private final Activity activity;
  private final String fileName;

  private ActivityBanner(
      UUID id,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Activity activity,
      Instant createdAt,
      Instant updatedAt) {
    super(
        id,
        null,
        EFileCategory.TRACE_ATTACHEMENT,
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
    this.activity = activity;
    this.fileName = fileName;
  }

  public static ActivityBanner create(
      UUID id,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      String uri,
      User uploadedBy,
      Activity activity) {
    Instant now = Instant.now();
    return new ActivityBanner(
        id, fileName, fileType, size, version, true, uri, uploadedBy, now, activity, now, now);
  }

  public static ActivityBanner toDomain(
      UUID id,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      Activity activity,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityBanner(
        id,
        fileName,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        activity,
        createdAt,
        updatedAt);
  }
}
