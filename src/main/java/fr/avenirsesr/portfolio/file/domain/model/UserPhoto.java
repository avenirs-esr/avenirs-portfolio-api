package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPhoto extends File {
  private final User user;
  private final EUserCategory userCategory;
  private final EUserPhotoType userPhotoType;

  private UserPhoto(
      UUID id,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      User user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType) {
    super(id, fileType, size, version, isActiveVersion, uri, uploadedBy, uploadedAt);
    this.user = user;
    this.userCategory = userCategory;
    this.userPhotoType = userPhotoType;
  }

  public static UserPhoto create(
      UUID id,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      User user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType) {
    return new UserPhoto(
        id,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        Instant.now(),
        user,
        userCategory,
        userPhotoType);
  }

  public static UserPhoto toDomain(
      UUID id,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      User user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType) {
    return new UserPhoto(
        id,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        user,
        userCategory,
        userPhotoType);
  }
}
