package fr.avenirsesr.portfolio.file.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.File;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPhoto extends File {
  private final User user;
  private final String name;
  private final EUserCategory userCategory;
  private final EUserPhotoType userPhotoType;

  private UserPhoto(
      UUID id,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      User user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
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
    this.user = user;
    this.userCategory = userCategory;
    this.userPhotoType = userPhotoType;
    this.name = name;
  }

  public static UserPhoto create(
      UUID id,
      String name,
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
        name,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        Instant.now(),
        user,
        userCategory,
        userPhotoType,
        Instant.now(),
        Instant.now());
  }

  public static UserPhoto toDomain(
      UUID id,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      User uploadedBy,
      Instant uploadedAt,
      User user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
      Instant createdAt,
      Instant updatedAt) {
    return new UserPhoto(
        id,
        name,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        user,
        userCategory,
        userPhotoType,
        createdAt,
        updatedAt);
  }
}
