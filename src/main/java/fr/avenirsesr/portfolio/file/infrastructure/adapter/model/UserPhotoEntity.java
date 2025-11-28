package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_photo",
    indexes = {
      @Index(name = "idx_user_photo_user", columnList = "user_id"),
      @Index(
          name = "idx_user_photo_user_type_active",
          columnList = "user_id, user_photo_type, is_active_version"),
      @Index(name = "idx_user_photo_user_version", columnList = "user_id, version DESC")
    })
@NoArgsConstructor
@Getter
@Setter
public class UserPhotoEntity extends FileEntity {

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "user_category")
  private EUserCategory userCategory;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "user_photo_type")
  private EUserPhotoType userPhotoType;

  private UserPhotoEntity(
      UUID id,
      String name,
      UserEntity user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt) {
    this.setId(id);
    this.name = name;
    this.user = user;
    this.userCategory = userCategory;
    this.userPhotoType = userPhotoType;
    this.setFileType(fileType);
    this.setSize(size);
    this.setVersion(version);
    this.setActiveVersion(isActiveVersion);
    this.setUri(uri);
    this.setUploadedBy(uploadedBy);
    this.setUploadedAt(uploadedAt);
  }

  public static UserPhotoEntity of(
      UUID id,
      String name,
      UserEntity user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt) {
    return new UserPhotoEntity(
        id,
        name,
        user,
        userCategory,
        userPhotoType,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt);
  }
}
