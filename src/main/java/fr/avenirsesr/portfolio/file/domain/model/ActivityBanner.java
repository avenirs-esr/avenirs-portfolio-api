package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityBanner extends File {
  private final Activity activity;

  private ActivityBanner(
      UUID id,
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
  }

  public static ActivityBanner create(
      UUID id,
      EFileType fileType,
      long size,
      int version,
      String uri,
      User uploadedBy,
      Activity activity) {
    Instant now = Instant.now();
    return new ActivityBanner(
        id, fileType, size, version, true, uri, uploadedBy, now, activity, now, now);
  }

  public static ActivityBanner toDomain(
      UUID id,
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
